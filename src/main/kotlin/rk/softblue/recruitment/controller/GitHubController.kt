package rk.softblue.recruitment.controller

import io.ktor.client.call.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rk.softblue.recruitment.model.RepoDetails
import rk.softblue.recruitment.service.GitHubService

fun Application.configureRouting(gitHubService: GitHubService) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/repositories/{owner}/{repositoryname}") {
            val owner = call.parameters["owner"]
            val repositoryName = call.parameters["repositoryname"]

            if (owner == null) {
                throw IllegalArgumentException("Owner must be not null!")
            }

            if (repositoryName == null) {
                throw IllegalArgumentException("Repo name must be not null!")
            }

            val response = gitHubService.getRepoDetails(owner.toString(), repositoryName.toString())
            call.respond(response.body() as RepoDetails)
        }
    }
}