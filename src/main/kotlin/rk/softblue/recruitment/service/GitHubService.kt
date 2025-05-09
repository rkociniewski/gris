package rk.softblue.recruitment.service

import rk.softblue.recruitment.model.RepoDetails

interface GitHubService {
    suspend fun getRepoDetails(owner: String, repoName: String): Result<RepoDetails>
}
