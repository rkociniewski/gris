package rk.powermilk.gris.model

import io.ktor.http.HttpStatusCode

/**
 * Data class representing an error response from the API.
 *
 * @property message The error message to be shown to the client
 * @property code The HTTP status code as an integer
 * @property status The HTTP status description
 */
data class ErrorResponse(
    val message: String,
    val code: Int,
    val status: String,
) {
    /**
     * Secondary constructor that creates an ErrorResponse from a message and HttpStatusCode.
     *
     * @param message The error message
     * @param statusCode The HTTP status code object
     */
    constructor(message: String, statusCode: HttpStatusCode) : this(message, statusCode.value, statusCode.description)
}
