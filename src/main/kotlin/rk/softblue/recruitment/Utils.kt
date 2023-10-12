package rk.softblue.recruitment

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.*
import rk.softblue.recruitment.model.JsonMapper

private val notFoundException = NotFoundException("Owner or repo name doesn't found.")

val client = HttpClient(CIO) {
    expectSuccess = true
    HttpResponseValidator {
        validateResponse { response ->
            if (response.status == HttpStatusCode.NotFound) {
                throw notFoundException
            }
        }

        handleResponseExceptionWithRequest { exception, _ ->
            val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
            val exceptionResponse = clientException.response

            if (exceptionResponse.status == HttpStatusCode.NotFound) {
                throw notFoundException
            }
        }

    }

    install(Logging) {
        level = LogLevel.INFO
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(JsonMapper.defaultMapper))
    }
}