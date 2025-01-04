package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.call.body
import org.junit.jupiter.api.Assertions.assertEquals
import rk.softblue.recruitment.TestEntities.exampleRepoDetails
import rk.softblue.recruitment.model.RepoDetails
import kotlin.test.Test

class JsonMapperTest : BaseUnitTest() {
    @Test
    fun `should map GitHub API response to RepoDetails`() = withTest {
        // Getting mapped response from GitHub API
        val response = service.getRepoDetails("not", "exists").body<RepoDetails>()

        // Checking verify
        assertEquals(exampleRepoDetails.fullName, response.fullName)
        assertEquals(exampleRepoDetails.description, response.description)
        assertEquals(exampleRepoDetails.cloneUrl, response.cloneUrl)
        assertEquals(exampleRepoDetails.stars, response.stars)
        assertEquals(exampleRepoDetails.createdAt, response.createdAt)
        kotlin.test.assertEquals("https://api.github.com/repos/not/exists", capturedRequests.firstOrNull())
    }
}