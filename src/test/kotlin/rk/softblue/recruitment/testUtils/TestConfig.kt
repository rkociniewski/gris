package rk.softblue.recruitment.testUtils

import io.ktor.http.HttpStatusCode

data class TestConfig(
    val statusCode: HttpStatusCode,
    val testDelay: Boolean,
    val invalidJson: Boolean,
    val emptyJson: Boolean
)