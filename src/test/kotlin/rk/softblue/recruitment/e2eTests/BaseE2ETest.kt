package rk.softblue.recruitment.e2eTests

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.KtorDsl
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import rk.softblue.recruitment.config.configureErrorHandling
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.configureKoin
import rk.softblue.recruitment.controller.configureGHRouting
import rk.softblue.recruitment.di.notFoundException
import rk.softblue.recruitment.model.JsonMapper.gitHubResponseMapper
import rk.softblue.recruitment.model.RepoDetails
import rk.softblue.recruitment.testUtils.TestEntities.repoDetailsNotFound
import rk.softblue.recruitment.testUtils.TestEntities.repoDetailsOK
import kotlin.test.AfterTest

open class BaseE2ETest : KoinTest {
    internal lateinit var e2eclient: io.ktor.client.HttpClient

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @KtorDsl
    fun withTest(status: HttpStatusCode = HttpStatusCode.OK, block: suspend ApplicationTestBuilder.() -> Unit) {
        val repoDetails = when (status) {
            HttpStatusCode.OK -> repoDetailsOK
            HttpStatusCode.NotFound -> repoDetailsNotFound
            else -> RepoDetails()
        }

        testApplication {
            // Utworzenie klienta z serializacją i logowaniem
            e2eclient = createClient {
                install(Logging) {
                    level = LogLevel.INFO
                }

                install(ContentNegotiation) {
                    register(ContentType.Application.Json, JacksonConverter(gitHubResponseMapper))
                }

                defaultRequest {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }

                HttpResponseValidator {
                    validateResponse { response ->
                        if (response.status == HttpStatusCode.NotFound) {
                            throw notFoundException
                        }
                    }
                    handleResponseExceptionWithRequest { exception, _ ->
                        val clientException =
                            exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                        if (clientException.response.status == HttpStatusCode.NotFound) {
                            throw notFoundException
                        }
                    }
                }
            }

            // Konfiguracja aplikacji testowej
            application {
                configureKoin(e2eclient)
                configureSerialization()
                configureMonitoring()
                configureErrorHandling()
                routing {
                    get("/") {
                        call.respondText("Hello World!")
                    }
                }
            }

            environment {
                config = MapApplicationConfig("ktor.environment" to "dev")
            }

            // Mockowanie zewnętrznego API GitHub
            externalServices {
                hosts("https://api.github.com") {
                    configureSerialization()

                    routing {
                        get("repos/{owner}/{repoName}") {
                            call.respond(status, repoDetails)
                        }
                    }
                }
            }

            // Ręczne podpięcie klienta do Koin
            startKoin {
                modules(module {
                    single { e2eclient }
                })
            }

            block()
        }
    }
}
