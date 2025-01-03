package rk.softblue.recruitment

import io.ktor.http.*
import rk.softblue.recruitment.model.ErrorResponse
import rk.softblue.recruitment.model.RepoDetails
import java.time.LocalDateTime

object TestEntities {
    val endpointNotFoundResponse = ErrorResponse(
        "Endpoint not found. Check spelling.", HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description
    )
    val repoDetailsNotFound = ErrorResponse(
        "Owner or repo name doesn't found.", HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description
    )

    val repoDetailsOK = RepoDetails(
        "rkociniewski/prime",
        null,
        "https://github.com/rkociniewski/prime.git",
        0,
        LocalDateTime.of(2023, 3, 6, 22, 16, 48)
    )
}