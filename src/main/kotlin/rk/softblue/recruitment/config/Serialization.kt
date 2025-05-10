package rk.softblue.recruitment.config

import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import rk.softblue.recruitment.model.JsonMapper

/**
 * Configures JSON serialization and deserialization for the application.
 *
 * This function installs the ContentNegotiation plugin with Jackson as the JSON
 * processor. It uses a preconfigured ObjectMapper from [JsonMapper] that has been
 * specifically set up for handling GitHub API responses.
 *
 * The configuration ensures consistent JSON handling across the application,
 * with proper date formatting and other serialization settings.
 *
 * @receiver Application The Ktor application instance.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            register(ContentType.Application.Json, JacksonConverter(JsonMapper.gitHubResponseMapper))
        }
    }
}
