package rk.softblue.recruitment.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import rk.softblue.recruitment.model.ErrorResponse

@Suppress("LongMethod")
fun Application.configureErrorHandling() {
    install(StatusPages) {
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
