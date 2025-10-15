package rk.powermilk.gris

import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class ApplicationTest {

    @Test
    fun `application module should configure successfully`() = testApplication {
        application {
            module()
        }

        assertNotNull(application)
    }
}
