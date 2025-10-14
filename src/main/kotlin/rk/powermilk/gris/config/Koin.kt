package rk.powermilk.gris.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import rk.powermilk.gris.di.appModule
import rk.powermilk.gris.di.notFoundException
import rk.powermilk.gris.model.JsonMapper

/**
 * Preconfigured an HTTP client for making API requests.
 *
 * Features:
 * - Expects successful responses (throws exceptions for non-2xx responses)
 * - Custom error handling for 404 responses
 * - Logging of requests and responses
 * - JSON content negotiation using Jackson
 */
private val appClient = HttpClient(CIO) {
    expectSuccess = true
    HttpResponseValidator {
        validateResponse { response ->
            if (response.status == HttpStatusCode.NotFound) {
                throw notFoundException
            }
        }
        handleResponseExceptionWithRequest { exception, _ ->
            val clientException =
                exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
            if (clientException.response.status == HttpStatusCode.NotFound) {
                throw notFoundException
            }
        }
    }

    install(Logging) {
        level = LogLevel.INFO
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(JsonMapper.gitHubResponseMapper))
    }
}

/**
 * Configures dependency injection using Koin.
 *
 * This function sets up the Koin dependency injection framework with the application module.
 * It allows for an optional custom HTTP client to be provided, which is useful for testing.
 *
 * @param httpClient The HTTP client to use for API requests. Defaults to [appClient].
 * @receiver Application The Ktor application instance.
 */
fun Application.configureKoin(httpClient: HttpClient = appClient) {
    install(Koin) {
        // Adding modules
        modules(appModule(httpClient))
    }
}
