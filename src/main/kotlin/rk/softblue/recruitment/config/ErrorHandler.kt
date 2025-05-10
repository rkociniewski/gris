package rk.softblue.recruitment.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import rk.softblue.recruitment.model.ErrorResponse

/**
 * Configures global error handling for the application.
 *
 * This function sets up exception handling for various error scenarios by installing
 * the StatusPages plugin.
 *
 * It handles common exceptions like IllegalArgumentException
 * and NotFoundException, as well as generic Throwable instances.
 *
 * Additionally, it configures custom responses for specific HTTP status codes to provide
 * consistent error formatting across the application.
 *
 * @receiver Application The Ktor application instance.
 */
@Suppress("LongMethod")
fun Application.configureErrorHandling() {
    install(StatusPages) {
        // Exception handling configuration
        exception<Throwable> { call, throwable ->
            when (throwable) {
                is IllegalArgumentException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            throwable.message ?: "Bad Request", HttpStatusCode.BadRequest
                        )
                    )
                }

                is NotFoundException -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(
                            throwable.message ?: "Not Found", HttpStatusCode.NotFound
                        )
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(
                            throwable.message ?: "Unexpected error",
                            call.response.status() ?: HttpStatusCode.InternalServerError
                        )
                    )
                }
            }
        }

        // HTTP status code handling configuration
        status(
            HttpStatusCode.InternalServerError,
            HttpStatusCode.NotFound,
            HttpStatusCode.BadRequest,
            HttpStatusCode.OK
        ) { call, statusCode ->
            when (statusCode) {
                HttpStatusCode.InternalServerError -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Internal server error", statusCode)
                    )
                }

                HttpStatusCode.BadRequest -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Request can't be handled.", statusCode)
                    )
                }

                HttpStatusCode.NotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(
                            "Endpoint not found. Check spelling.", statusCode
                        )
                    )
                }
            }
        }
    }
}
