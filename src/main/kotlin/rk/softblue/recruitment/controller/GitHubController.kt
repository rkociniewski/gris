package rk.softblue.recruitment.controller

import io.ktor.client.call.body
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import rk.softblue.recruitment.model.RepoDetails
import rk.softblue.recruitment.service.GitHubService

fun Application.configureGHRouting() {
    val gitHubService: GitHubService by inject()

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