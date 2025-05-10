package rk.softblue.recruitment.service

import rk.softblue.recruitment.model.RepoDetails

/**
 * Service interface for accessing GitHub repository information.
 * Provides methods to fetch repository details from the GitHub API.
 */
interface GitHubService {
    /**
     * Retrieves details for a specific GitHub repository.
     *
     * @param owner The GitHub username or organization that owns the repository
     * @param repoName The name of the repository
     * @return A Result containing either the repository details or an exception if the operation failed
     */
    suspend fun getRepoDetails(owner: String, repoName: String): Result<RepoDetails>
}
