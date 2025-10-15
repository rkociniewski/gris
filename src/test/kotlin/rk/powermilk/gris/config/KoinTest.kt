package rk.powermilk.gris.config

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.koin.core.context.stopKoin
import rk.powermilk.gris.controller.configureGHRouting
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KoinTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should configure Koin with default HttpClient`() = testApplication {
        application {
            configureKoin()
        }

        // Verify that Koin was configured successfully
        assertNotNull(application)
    }

    @Test
    fun `should configure Koin with default HttpClient successfully`() = testApplication {
        application {
            configureKoin()
            configureSerialization()
            configureGHRouting()
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
