package rk.softblue.recruitment.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.calllogging.processingTimeMillis
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level

private val logger = KotlinLogging.logger {}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO

        // Logowanie wszystkich zapytań HTTP
        filter { call ->
            true
        }

        // Dodatkowe informacje o zapytaniu
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val path = call.request.path()
            val userAgent = call.request.headers["User-Agent"]
            val duration = call.processingTimeMillis()

            "Request: $httpMethod $path, Status: $status, Duration: $duration ms, User-Agent: $userAgent"
        }
    }

    // Logowanie informacji o uruchomieniu aplikacji
    this.monitor.subscribe(ApplicationStarted) {
        logger.info { "Application started on port ${environment.config.property("ktor.deployment.port").getString()}" }
    }

    // Logowanie informacji o zatrzymaniu aplikacji
    this.monitor.subscribe(ApplicationStopped) {
        logger.info { "Application stopped" }
    }
}
