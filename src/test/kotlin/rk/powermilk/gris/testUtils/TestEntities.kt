package rk.powermilk.gris.testUtils

import io.ktor.http.HttpStatusCode
import rk.powermilk.gris.model.ErrorResponse
import rk.powermilk.gris.model.RepoDetails
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TestEntities {
    val endpointNotFoundResponse = ErrorResponse(
        "Endpoint not found. Check spelling.", HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description
    )
    val repoDetailsNotFound = ErrorResponse(
        "Owner or repo name doesn't found.", HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description
    )

    val repoDetailsOK = RepoDetails(
        "rkociniewski/prime",
        "",
        "https://github.com/rkociniewski/prime.git",
        0,
        LocalDateTime.parse("2023-03-06T22:16:48.000", DateTimeFormatter.ISO_DATE_TIME)
    )

    val exampleRepoDetails = RepoDetails(
        "example/repo",
        "An Example Repo description",
        "https://github.com/example/repo.git",
        2137,
        LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

    val repoDetailsTest = RepoDetails(
        "test/repo",
        "Test description",
        "https://test.url",
        10,
        LocalDateTime.of(2020, 1, 1, 12, 0)
    )

    val repoDetailsSymmetryTest = RepoDetails(
        fullName = "symmetry/test",
        description = "Symmetry check",
        cloneUrl = "https://symmetry.url",
        stars = 7,
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
    )

    val repoDetailsTestJson = """
        {
            "full_name": "test/repo",
            "description": "Test description",
            "clone_url": "https://test.url",
            "stargazers_count": 10,
            "created_at": "2020-01-01T12:00:00"
        }
        """.trimIndent().replace(Regex("\\s"), "")

    val nullJson = """
        {
            "full_name": null,
            "description": null,
            "clone_url": null,
            "stargazers_count": null,
            "created_at": null
        }
        """.trimIndent()


    val invalidDateJson = """
        {
          "full_name": "user/repo",
          "description": "desc",
          "clone_url": "url",
          "stargazers_count": 0,
          "created_at": "invalid-date"
        }
    """.trimIndent()


    val zonedDateTimeJson = """
        {
          "full_name": "rkociniewski/prime",
          "description": null,
          "clone_url": "https://github.com/rkociniewski/prime.git",
          "stargazers_count": 0,
          "created_at": "2023-03-06T22:16:48Z"
        }
    """.trimIndent()

    val invalidStarsJson = """
        {
            "full_name": "rkociniewski/prime",
            "description": null,
            "clone_url": "https://github.com/rkociniewski/prime.git",
            "stargazers_count": "invalid",
            "created_at": "2023-03-06T22:16:48"
        }
    """
}
