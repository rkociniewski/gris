package rk.powermilk.gris.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

/**
 * Logger instance for this file.
 */
private val logger = KotlinLogging.logger {}

/**
 * Configures OpenAPI documentation and Swagger UI for the application.
 *
 * This function sets up:
 * 1. CORS (Cross-Origin Resource Sharing) to allow access from any host
 * 2. An endpoint to serve the raw OpenAPI YAML specification
 * 3. An endpoint to serve the interactive Swagger UI
 *
 * The OpenAPI specification is loaded from the "openapi/documentation.yaml" file in the resources folder.
 *
 * Once configured, the API documentation will be available at:
 * - /openapi - Raw YAML OpenAPI specification
 * - /swagger - Interactive Swagger UI
 *
 * @receiver Application The Ktor application instance.
 */
fun Application.configureSwagger() {
    logger.info { "Configuring Swagger documentation" }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        // Endpoint for the raw OpenAPI YAML specification
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            logger.debug { "OpenAPI YAML requested" }
        }

        // Endpoint for the interactive Swagger UI
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            logger.debug { "Swagger UI requested" }
        }
    }

    logger.info { "Swagger documentation available at /swagger" }
}
