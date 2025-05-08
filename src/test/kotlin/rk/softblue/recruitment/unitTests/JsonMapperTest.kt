package rk.softblue.recruitment.unitTests

import BaseUnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import rk.softblue.recruitment.testUtils.TestEntities.exampleRepoDetails
import rk.softblue.recruitment.testUtils.UnitTestBuilder
import kotlin.test.Test

class JsonMapperTest : BaseUnitTest() {
    @Test
    fun `should map GitHub API response to RepoDetails`() = UnitTestBuilder().build().withTest {
        val result = service.getRepoDetails("not", "exists")

        assertTrue(result.isSuccess)
        val response = result.getOrThrow()

        assertEquals(exampleRepoDetails.fullName, response.fullName)
        assertEquals(exampleRepoDetails.description, response.description)
        assertEquals(exampleRepoDetails.cloneUrl, response.cloneUrl)
        assertEquals(exampleRepoDetails.stars, response.stars)
        assertEquals(exampleRepoDetails.createdAt, response.createdAt)
        assertEquals("https://api.github.com/repos/not/exists", capturedRequests.firstOrNull())
    }
}
