package rk.softblue.recruitment.model

data class ErrorResponse(
    val message: String,
    val code: Int,
    val status: String,
)