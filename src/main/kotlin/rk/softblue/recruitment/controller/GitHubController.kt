package rk.softblue.recruitment.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import rk.softblue.recruitment.service.GitHubService

private val logger = KotlinLogging.logger {}

@Suppress("ThrowsCount")
fun Application.configureGHRouting() {
    val gitHubService: GitHubService by inject()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/repositories/{owner}/{repoName}") {
            val owner = call.parameters["owner"]
            val repoName = call.parameters["repoName"]

            logger.info { "Received request for repository details: $owner/$repoName" }

            if (owner.isNullOrBlank() || repoName.isNullOrBlank()) {
                logger.warn { "Invalid request parameters - owner: $owner, repository: $repoName" }
                call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
                return@get
            }

            val result = gitHubService.getRepoDetails(owner, repoName)

            result.fold(
                onSuccess = { call.respond(it) },
                onFailure = { throwable ->
                    when (throwable) {
                        is NotFoundException -> throw throwable
                        else -> throw BadRequestException("Unexpected error", throwable)
                    }
                }
            )
        }
    }
}
