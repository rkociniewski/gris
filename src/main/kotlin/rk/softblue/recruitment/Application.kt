package rk.softblue.recruitment

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
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin
import rk.softblue.recruitment.config.configureErrorHandling
import rk.softblue.recruitment.config.configureMonitoring
import rk.softblue.recruitment.config.configureSerialization
import rk.softblue.recruitment.controller.configureGHRouting
import rk.softblue.recruitment.di.appModule
import rk.softblue.recruitment.di.notFoundException
import rk.softblue.recruitment.model.JsonMapper

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

fun main() {
    run()
}

fun run() = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)

fun Application.module() {
    configureKoin()
    configureGHRouting()
    configureSerialization()
    configureMonitoring()
    configureErrorHandling()
}

fun Application.configureKoin(httpClient: HttpClient = appClient) {
    install(Koin) {
        // Adding modules
        modules(appModule(httpClient))
    }
}
