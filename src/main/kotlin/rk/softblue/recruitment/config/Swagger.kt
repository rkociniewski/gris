package rk.softblue.recruitment.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

private val logger = KotlinLogging.logger {}

fun Application.configureSwagger() {
    logger.info { "Configuring Swagger documentation" }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        // Endpoint do pliku OpenAPI YAML
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            logger.debug { "OpenAPI YAML requested" }
        }

        // Endpoint do interfejsu Swagger UI
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            logger.debug { "Swagger UI requested" }
        }
    }

    logger.info { "Swagger documentation available at /swagger" }
}
