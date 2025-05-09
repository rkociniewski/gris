package rk.softblue.recruitment.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import rk.softblue.recruitment.model.RepoDetails

class GitHubServiceImpl : GitHubService, KoinComponent {
    private val client by inject<HttpClient>()

    override suspend fun getRepoDetails(owner: String, repoName: String): Result<RepoDetails> {
        println("GitHubServiceImpl Client config: ${client}")
        return try {
            val response = client.get("https://api.github.com/repos/$owner/$repoName")
            println("GitHubService response: status=${response.status}, body=${response.bodyAsText()}")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val repo = response.body<RepoDetails>()
                    println("Deserialized RepoDetails: $repo")
                    Result.success(repo)
                }
                HttpStatusCode.NotFound -> Result.failure(NotFoundException("Repository not found for $owner/$repoName"))
                else -> Result.failure(RuntimeException("Unexpected status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}