package rk.softblue.recruitment.testUtils

import io.ktor.http.HttpStatusCode

class UnitTestBuilder {
    private var statusCode = HttpStatusCode.OK
    private var testDelay: Boolean = false
    private var invalidJson = false
    private var emptyJson = false

    fun statusCode(statusCode: HttpStatusCode) = apply { this.statusCode = statusCode }
    fun delay(testDelay: Boolean) = apply { this.testDelay = testDelay }
    fun invalidJson(invalidJson: Boolean) = apply { this.invalidJson = invalidJson }
    fun emptyJson(emptyJson: Boolean) = apply { this.emptyJson = emptyJson }
    fun build() = TestConfig(statusCode, testDelay, invalidJson, emptyJson)
}