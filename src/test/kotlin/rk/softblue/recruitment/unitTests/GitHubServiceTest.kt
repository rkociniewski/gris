package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.plugins.NotFoundException
import rk.softblue.recruitment.TestEntities.exampleRepoDetails
import rk.softblue.recruitment.di.notFoundException
import rk.softblue.recruitment.model.RepoDetails
import kotlin.test.*

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
    fun `should handle empty response`() = withTest(emptyJson = true) {
        val response = service.getRepoDetails("owner", "empty-repo")
        assertNotNull(response)
    }

    @Test
    fun `should throw NotFoundException when repo does not exist`() = withTest(HttpStatusCode.NotFound) {
        // Assertion of throwable
        assertFailsWith<NotFoundException>(
            message = notFoundException.message,
            block = { service.getRepoDetails("owner", "nonexistent-repo") }
        )
    }

    @Test
    fun `should handle Internal Server Error`() = withTest(HttpStatusCode.InternalServerError) {
        val exception = assertFailsWith<ServerResponseException> {
            service.getRepoDetails("owner", "repo")
        }
        assertEquals(HttpStatusCode.InternalServerError, exception.response.status)
    }

    @Test
    fun `should handle Unauthorized (401) response`() = withTest(HttpStatusCode.Unauthorized) {
        val exception = assertFailsWith<ClientRequestException> {
            service.getRepoDetails("owner", "repo")
        }
        assertEquals(HttpStatusCode.Unauthorized, exception.response.status)
    }

    @Test
    fun `should handle Forbidden (403) response`() = withTest(HttpStatusCode.Forbidden) {
        val exception = assertFailsWith<ClientRequestException> {
            service.getRepoDetails("owner", "repo")
        }
        assertEquals(HttpStatusCode.Forbidden, exception.response.status)
    }

    @Test
    fun `should fail on malformed JSON`() = withTest (invalidJson = true) {
        val exception = assertFailsWith<JsonConvertException> {
            service.getRepoDetails("owner", "broken-repo").body<RepoDetails>()
        }

        assertContains(exception.message ?: "", "Unexpected end-of-input")
    }

    @Test
    fun `should fail on request timeout`() = withTest(delay = true) {
        assertFailsWith<HttpRequestTimeoutException> {
            service.getRepoDetails("owner", "timeout-repo")
        }
    }
}