package rk.softblue.recruitment.di

import io.ktor.client.HttpClient
import io.ktor.server.plugins.NotFoundException
import org.koin.dsl.module
import rk.softblue.recruitment.service.GitHubService
import rk.softblue.recruitment.service.GitHubServiceImpl

val notFoundException = NotFoundException("Owner or repo name doesn't found.")

fun appModule(httpClient: HttpClient) = module {
    single<GitHubService> { GitHubServiceImpl() }

    single {
        httpClient
    }
}