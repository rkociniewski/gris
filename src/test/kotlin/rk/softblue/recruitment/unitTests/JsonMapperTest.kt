package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.call.body
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import rk.softblue.recruitment.TestEntities.exampleRepoDetails
import rk.softblue.recruitment.model.RepoDetails
import kotlin.test.Test

class JsonMapperTest : BaseUnitTest() {
    @Test
    fun `should map GitHub API response to RepoDetails`() = runTest {
        val response = service.getRepoDetails("not", "exist").body<RepoDetails>()
        assertEquals(exampleRepoDetails.fullName, response.fullName)
        assertEquals(exampleRepoDetails.description, response.description)
        assertEquals(exampleRepoDetails.cloneUrl, response.cloneUrl)
        assertEquals(exampleRepoDetails.stars, response.stars)
        assertEquals(exampleRepoDetails.createdAt, response.createdAt)
    }
}