package rk.softblue.recruitment.config

import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import rk.softblue.recruitment.model.JsonMapper

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(JsonMapper.gitHubResponseMapper))
    }
}