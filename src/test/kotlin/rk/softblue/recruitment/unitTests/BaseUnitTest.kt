import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import rk.softblue.recruitment.service.GitHubService
import rk.softblue.recruitment.service.GitHubServiceImpl
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseUnitTest : KoinTest {

    @BeforeTest
    fun setup() {
        startKoin {
            modules(
                module {
                    single { client }
                    single<GitHubService> { GitHubServiceImpl() }
                }
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        capturedRequests.clear()
    }

    // Capture request to validate later
    val capturedRequests = mutableListOf<String>()

    // Creating MockEngine with simulated response.
    private val mockEngine = MockEngine {
        capturedRequests.add(it.url.toString())
        respond(
            content = "Mocked Response",
            status = HttpStatusCode.OK,
            headers = headersOf("Content-Type" to listOf("application/json"))
        )
    }

    // Creating HttpClient from MockEngine
    private val client = HttpClient(mockEngine)

    // Mocking GitHubService and inject dependencies
    val service: GitHubService by inject()
}