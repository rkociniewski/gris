package rk.powermilk.gris.di

import io.ktor.client.HttpClient
import io.ktor.server.plugins.NotFoundException
import org.koin.dsl.module
import rk.powermilk.gris.service.GitHubService
import rk.powermilk.gris.service.GitHubServiceImpl

/**
 * Default NotFoundException used when repository owner or name is not found
 */
val notFoundException = NotFoundException("Owner or repo name doesn't found.")

/**
 * Defines the application's dependency injection module.
 * Configures the services and components needed by the application.
 *
 * @param httpClient The HTTP client to be used for external service calls
 * @return Koin module with configured dependencies
 */
fun appModule(httpClient: HttpClient) = module {
    // Register GitHubService implementation as a singleton
    single<GitHubService> { GitHubServiceImpl() }

    // Provide the HTTP client as a singleton
    single {
        httpClient
    }
}
