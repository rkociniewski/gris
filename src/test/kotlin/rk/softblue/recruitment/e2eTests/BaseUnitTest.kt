package rk.softblue.recruitment.e2eTests

import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.util.*
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.config.errorHandling
import rk.softblue.recruitment.controller.configureRouting
import rk.softblue.recruitment.service.RKGithubService

open class BaseUnitTest {
    @KtorDsl
    fun withTest(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            application {
                configureRouting(RKGithubService())
                configureSerialization()
                configureMonitoring()
                errorHandling()
            }

            environment {
                config = MapApplicationConfig("ktor.environment" to "dev")
            }

            block()
        }
    }
}