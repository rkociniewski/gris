package rk.powermilk.gris.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import rk.powermilk.gris.model.RepoDetails

/**
 * Implementation of the GitHubService interface that interacts with the GitHub REST API.
 * This service handles HTTP requests to fetch repository information and maps responses to domain objects.
 *
 * The implementation uses Koin for dependency injection to obtain an HttpClient instance.
 * Various HTTP status codes from the GitHub API are handled appropriately with meaningful error messages.
 */
@Suppress("TooGenericExceptionCaught")
class GitHubServiceImpl : GitHubService, KoinComponent {
    private val client by inject<HttpClient>()
    private val logger = KotlinLogging.logger {}

    /**
     * Fetches GitHub repository details via the GitHub REST API.
     *
     * @param owner The GitHub username or organization that owns the repository
     * @param repoName The name of the repository
     * @return A Result containing the repository details if successful, or an appropriate exception if failed
     *
     * Possible error cases:
     * - NotFoundException: When the requested repository doesn't exist
     * - BadRequestException: When GitHub API rate limit is exceeded
     * - RuntimeException: For other unexpected API responses
     */
    override suspend fun getRepoDetails(owner: String, repoName: String): Result<RepoDetails> {
        logger.info { "Fetching repo: $owner/$repoName" }
        val response = client.get("https://api.github.com/repos/$owner/$repoName")
        logger.info { "GitHub response: ${response.status}" }

        return when (response.status) {
            HttpStatusCode.OK -> {
                logger.info { "Successfully fetched repository data from GitHub API: $owner/$repoName" }
                Result.success(response.body())
            }

            HttpStatusCode.NotFound -> {
                logger.warn { "Repository not found on GitHub: $owner/$repoName" }
                Result.failure(NotFoundException("Repository not found for $owner/$repoName"))
            }

            HttpStatusCode.Forbidden, HttpStatusCode.TooManyRequests -> {
                logger.warn { "Rate limit exceeded for GitHub API (status: ${response.status})" }
                Result.failure(BadRequestException("GitHub API rate limit exceeded"))
            }

            else -> {
                logger.error { "Unexpected response from GitHub API: ${response.status}" }
                Result.failure(RuntimeException("GitHub API error: ${response.status}"))
            }
        }
    }
}
