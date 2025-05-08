package rk.softblue.recruitment.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.server.plugins.NotFoundException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import rk.softblue.recruitment.model.RepoDetails

class GitHubServiceImpl : GitHubService, KoinComponent {
    private val client by inject<HttpClient>()

    override suspend fun getRepoDetails(owner: String, repoName: String): Result<RepoDetails> {
        return try {
            val repo = client.get("https://api.github.com/repos/$owner/$repoName")
                .body<RepoDetails>()
            Result.success(repo)
        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}