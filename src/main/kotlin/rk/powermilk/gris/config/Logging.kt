package rk.powermilk.gris.config

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

/**
 * Logger instance for this file.
 */
private val logger = KotlinLogging.logger {}

/**
 * Configures request logging and application lifecycle monitoring.
 *
 * This function sets up:
 * 1. HTTP request logging with detailed information about each request
 * 2. Application lifecycle event logging (start and stop events)
 *
 * The logging includes:
 * - HTTP method and path
 * - Response status code
 * - Request processing duration
 * - User-Agent header
 *
 * @receiver Application The Ktor application instance.
 */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO

        // Log all HTTP requests
        filter { call ->
            true
        }

        // Format log entries with additional request information
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val path = call.request.path()
            val userAgent = call.request.headers["User-Agent"]
            val duration = call.processingTimeMillis()

            "Request: $httpMethod $path, Status: $status, Duration: $duration ms, User-Agent: $userAgent"
        }
    }

    // Log application startup information
    this.monitor.subscribe(ApplicationStarted) {
        logger.info { "Application started on port ${environment.config.property("ktor.deployment.port").getString()}" }
    }

    // Log application shutdown information
    this.monitor.subscribe(ApplicationStopped) {
        logger.info { "Application stopped" }
    }
}
