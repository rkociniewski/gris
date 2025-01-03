package rk.softblue.recruitment.di

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
import io.ktor.server.plugins.NotFoundException
import org.koin.dsl.module
import rk.softblue.recruitment.model.JsonMapper
import rk.softblue.recruitment.service.GitHubService
import rk.softblue.recruitment.service.GitHubServiceImpl

private val notFoundException = NotFoundException("Owner or repo name doesn't found.")

private fun provideClient() = HttpClient(CIO) {
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
        register(ContentType.Application.Json, JacksonConverter(JsonMapper.defaultMapper))
    }
}

val appModule = module {
    single<GitHubService> { GitHubServiceImpl() }

    single {
        provideClient()
    }
}
