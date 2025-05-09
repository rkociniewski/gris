package rk.softblue.recruitment.e2eTests

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.KtorDsl
import kotlinx.serialization.json.Json
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
import rk.softblue.recruitment.service.GitHubService
import rk.softblue.recruitment.service.GitHubServiceImpl
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
                        val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
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
