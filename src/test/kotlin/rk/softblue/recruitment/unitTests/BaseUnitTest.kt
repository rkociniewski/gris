import io.ktor.client.HttpClient
import io.ktor.client.engine.config
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import rk.softblue.recruitment.di.notFoundException
import rk.softblue.recruitment.model.JsonMapper.gitHubResponseMapper
import rk.softblue.recruitment.service.GitHubService
import rk.softblue.recruitment.service.GitHubServiceImpl
import kotlin.test.AfterTest

abstract class BaseUnitTest : KoinTest {
    private val exampleRepoDetailsJson = """
        {
            "full_name": "example/repo",
            "description": "An Example Repo description",
            "clone_url": "https://github.com/example/repo.git",
            "stargazers_count": 2137,
            "created_at": "2025-01-01T00:00:00Z"
        }
    """.trimIndent()

    private val malformedJson = exampleRepoDetailsJson.dropLast(1)

    @AfterTest
    fun tearDown() {
        stopKoin()
        capturedRequests.clear()
    }

    // Capture request to validate later
    val capturedRequests = mutableListOf<String>()

    // Mocking GitHubService and inject dependencies
    val service: GitHubService by inject()

    fun withTest(
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        delay: Boolean = false,
        invalidJson: Boolean = false,
        emptyJson: Boolean = false,
        block: suspend TestScope.() -> Unit
    ) {
        val byteChannelJson = when {
            invalidJson -> malformedJson
            emptyJson -> ""
            else -> exampleRepoDetailsJson
        }

        val mockEngine = MockEngine {
            if (delay) {
                delay(1000)
            }
            capturedRequests.add(it.url.toString())
            respond(
                content = ByteReadChannel(byteChannelJson),
                status = statusCode,
                headers = headersOf("Content-Type" to listOf("application/json"))
            )
        }

        // Creating HttpClient from MockEngine
        val client = HttpClient(mockEngine) {
            if (delay) {
                // Create an HttpClient with the MockEngine and configure the request timeout
                install(HttpTimeout) {
                    requestTimeoutMillis = 100 // Set the request timeout to 100 ms
                }
            }

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

            install(Logging) {
                level = LogLevel.INFO
            }

            install(ContentNegotiation) {
                register(ContentType.Application.Json, JacksonConverter(gitHubResponseMapper))
            }
        }

        startKoin {
            modules(
                module {
                    single { client }
                    single<GitHubService> { GitHubServiceImpl() }
                }
            )
        }

        runTest { block() }
    }
}