package rk.powermilk.gris.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import rk.powermilk.gris.service.GitHubService
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class AppModuleTest : KoinTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should create GitHubService instance from appModule`() {
        val mockEngine = MockEngine {
            respond(
                ByteReadChannel("""{"full_name":"test/repo"}"""),
                HttpStatusCode.OK,
                headersOf("Content-Type" to listOf("application/json"))
            )
        }

        val mockClient = HttpClient(mockEngine)

        startKoin {
            modules(appModule(mockClient))
        }

        val service: GitHubService by inject()
        assertNotNull(service)

        val client: HttpClient by inject()
        assertNotNull(client)
    }
}
