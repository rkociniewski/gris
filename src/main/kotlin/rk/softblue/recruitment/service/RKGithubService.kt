package rk.softblue.recruitment.service

import io.ktor.client.request.*
import io.ktor.client.statement.*
import rk.softblue.recruitment.client

class RKGithubService() : GitHubService {
    override suspend fun getRepoDetails(owner: String, repositoryName: String): HttpResponse {
        val response = client.get("https://api.github.com/repos/$owner/$repositoryName")
        client.close()

        return response
    }
}