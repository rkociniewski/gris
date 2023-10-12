package rk.softblue.recruitment.service

import io.ktor.client.statement.*

interface GitHubService {
    suspend fun getRepoDetails(owner: String, repositoryName: String): HttpResponse
}