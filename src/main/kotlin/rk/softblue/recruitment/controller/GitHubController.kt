package rk.softblue.recruitment.controller

import io.ktor.server.application.Application
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import rk.softblue.recruitment.service.GitHubService

fun Application.configureGHRouting() {
    val gitHubService: GitHubService by inject()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/repositories/{owner}/{repositoryname}") {
            val owner = call.parameters["owner"] ?: throw IllegalArgumentException("Owner must be not null!")
            val repoName = call.parameters["repositoryname"] ?: throw IllegalArgumentException("Repo name must be not null!")
            val result = gitHubService.getRepoDetails(owner, repoName)

            result.fold(
                onSuccess = {
                    println("Endpoint response: $it")
                    call.respond(it)
                },
                onFailure = { throwable ->
                    when (throwable) {
                        is NotFoundException -> throw throwable
                        else -> throw RuntimeException("Unexpected error", throwable)
                    }
                }
            )
        }
    }
}