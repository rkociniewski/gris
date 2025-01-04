package rk.softblue.recruitment.e2eTests

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.KtorDsl
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.config.errorHandling
import rk.softblue.recruitment.configureKoin
import rk.softblue.recruitment.controller.configureGHRouting
import rk.softblue.recruitment.di.appModule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class BaseE2ETest : KoinTest {
    @BeforeTest
    fun setup() {
        startKoin {
            modules(appModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @KtorDsl
    fun withTest(block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            application {
                configureKoin()
                configureGHRouting()
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