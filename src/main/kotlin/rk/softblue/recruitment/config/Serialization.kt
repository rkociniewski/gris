package rk.softblue.recruitment.config

import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.serialization.jackson.jackson
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import rk.softblue.recruitment.model.JsonMapper

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            register(ContentType.Application.Json, JacksonConverter(JsonMapper.gitHubResponseMapper))
        }

        register(
            ContentType.Text.Html, KotlinxSerializationConverter(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        )
    }
}