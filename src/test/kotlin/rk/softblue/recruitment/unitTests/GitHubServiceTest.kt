package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import rk.softblue.recruitment.TestEntities.exampleRepoDetails
import rk.softblue.recruitment.di.notFoundException
import rk.softblue.recruitment.model.RepoDetails
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GitHubServiceTest : BaseUnitTest() {

    @Test
    fun `should fetch repository details successfully`() = withTest {
        // Calling testing method
        val response = service.getRepoDetails("owner", "repository")
        // Verify result
        val body = response.body<RepoDetails>()
        // Verify response status code
        assertEquals(HttpStatusCode.OK, response.status)
        // Verify if the body isn't empty
        assertNotNull(body)
        // Verify that the mocked response was returned
        assertEquals(exampleRepoDetails, body)
        // Verify that the correct URL was used
        assertEquals("https://api.github.com/repos/owner/repository", capturedRequests.firstOrNull())
    }

    @Test
    fun `should throw NotFoundException when repo does not exist`() = withTest(HttpStatusCode.NotFound) {
        // Assertion of throwable
        assertFailsWith<NotFoundException>(
            message = notFoundException.message,
            block = { service.getRepoDetails("owner", "nonexistent-repo") }
        )
    }
}