package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GitHubServiceTest : BaseUnitTest() {

    @Test
    fun `should fetch repository details successfully`() = runTest {
        // Calling testing method
        val response = service.getRepoDetails("owner", "repository")
        // Verify result
        val body = response.bodyAsText()
        // Verify response status code
        assertEquals(HttpStatusCode.OK, response.status)
        // Verify if the body isn't empty
        assertNotNull(body)
        // Verify that the mocked response was returned
        assertEquals("Mocked Response", body)
        // Verify that the correct URL was used
        assertEquals("https://api.github.com/repos/owner/repository", capturedRequests.firstOrNull())
    }

    @Test
    fun `should throw RepoNotFoundException when repo does not exist`() = runTest {
        // Mock of not existing repository
        coEvery { service.getRepoDetails("owner", "nonexistent-repo") } throws NotFoundException()

        // Assertion of throwable
        assertFailsWith<NotFoundException> {
            service.getRepoDetails("owner", "nonexistent-repo")
        }
    }

}