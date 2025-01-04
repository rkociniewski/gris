package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
}