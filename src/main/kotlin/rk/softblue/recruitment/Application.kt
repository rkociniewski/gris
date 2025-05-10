package rk.softblue.recruitment

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import rk.softblue.recruitment.config.configureErrorHandling
import rk.softblue.recruitment.config.configureKoin
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.config.configureSwagger
import rk.softblue.recruitment.controller.configureGHRouting

/**
 * Application entry point.
 * Starts the Ktor server with the configured modules.
 */
fun main() {
    run()
}

/**
 * Initializes and runs the application server.
 * Sets up Netty embedded server on port 8080 and starts it.
 *
 * @return The configured server instance
 */
fun run() = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)

/**
 * Main application module configuration.
 * Sets up all necessary parts and features for the application.
 * This includes:
 * - Dependency injection (Koin)
 * - Routing for GitHub API endpoints
 * - JSON serialization
 * - Application monitoring
 * - Error handling
 * - API documentation (Swagger)
 */
fun Application.module() {
    configureKoin()
    configureGHRouting()
    configureSerialization()
    configureMonitoring()
    configureErrorHandling()
    configureSwagger()
}
