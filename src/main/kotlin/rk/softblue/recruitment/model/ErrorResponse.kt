package rk.softblue.recruitment.model

import io.ktor.http.HttpStatusCode

data class ErrorResponse(
    val message: String,
    val code: Int,
    val status: String,
) {
    constructor(message: String, statusCode: HttpStatusCode) : this(message, statusCode.value, statusCode.description)
}
