package rk.powermilk.gris.e2eTests

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.KtorDsl
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import rk.powermilk.gris.config.configureErrorHandling
import rk.powermilk.gris.config.configureMonitoring
import rk.powermilk.gris.config.configureSerialization
import rk.powermilk.gris.config.configureSwagger
import rk.powermilk.gris.controller.configureGHRouting
import rk.powermilk.gris.di.notFoundException
import rk.powermilk.gris.model.JsonMapper.gitHubResponseMapper
import rk.powermilk.gris.model.RepoDetails
import rk.powermilk.gris.service.GitHubService
import rk.powermilk.gris.service.GitHubServiceImpl
import rk.powermilk.gris.testUtils.TestEntities.repoDetailsNotFound
import rk.powermilk.gris.testUtils.TestEntities.repoDetailsOK
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
            e2eclient = createClient {
                install(Logging) { level = LogLevel.INFO }
                expectSuccess = true
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
                install(ContentNegotiation) {
                    register(ContentType.Application.Json, JacksonConverter(gitHubResponseMapper))
                }
            }

            application {
                configureSerialization()
                configureMonitoring()
                configureErrorHandling()
                configureGHRouting()
                configureSwagger()
            }

            environment {
                config = MapApplicationConfig("ktor.environment" to "dev")
            }

            externalServices {
                hosts("https://api.github.com") {
                    configureSerialization()
                    configureMonitoring()
                    configureErrorHandling()
                    routing {
                        get("repos/{owner}/{repoName}") {
                            call.respond(status, repoDetails)
                        }
                    }
                }
            }

            startKoin {
                modules(
                    module {
                        single { e2eclient }
                        single<GitHubService> { GitHubServiceImpl() }
                    }
                )
            }

            block()
        }
    }
}
