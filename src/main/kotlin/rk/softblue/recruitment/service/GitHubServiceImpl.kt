package rk.softblue.recruitment.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class GitHubServiceImpl : GitHubService, KoinComponent {
    private val client by inject<HttpClient>()

    override suspend fun getRepoDetails(owner: String, repositoryName: String): HttpResponse {
        val response = client.get("https://api.github.com/repos/$owner/$repositoryName")
        client.close()

        return response
    }
}