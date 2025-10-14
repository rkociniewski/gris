package rk.powermilk.gris.unitTests

import BaseUnitTest
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import rk.powermilk.gris.model.JsonMapper.gitHubResponseMapper
import rk.powermilk.gris.model.RepoDetails
import rk.powermilk.gris.testUtils.TestEntities.exampleRepoDetails
import rk.powermilk.gris.testUtils.TestEntities.invalidDateJson
import rk.powermilk.gris.testUtils.TestEntities.invalidStarsJson
import rk.powermilk.gris.testUtils.TestEntities.nullJson
import rk.powermilk.gris.testUtils.TestEntities.repoDetailsOK
import rk.powermilk.gris.testUtils.TestEntities.repoDetailsSymmetryTest
import rk.powermilk.gris.testUtils.TestEntities.repoDetailsTest
import rk.powermilk.gris.testUtils.TestEntities.repoDetailsTestJson
import rk.powermilk.gris.testUtils.TestEntities.zonedDateTimeJson
import rk.powermilk.gris.testUtils.UnitTestBuilder
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFailsWith

class JsonMapperTest : BaseUnitTest() {
    @Test
    fun `should map GitHub API response to RepoDetails`() = UnitTestBuilder().build().withTest {
        val result = service.getRepoDetails("not", "exists")

        val response = result.getOrThrow()

        assertEquals(exampleRepoDetails.fullName, response.fullName)
        assertEquals(exampleRepoDetails.description, response.description)
        assertEquals(exampleRepoDetails.cloneUrl, response.cloneUrl)
        assertEquals(exampleRepoDetails.stars, response.stars)
        assertEquals(exampleRepoDetails.createdAt, response.createdAt)
        assertEquals("https://api.github.com/repos/not/exists", capturedRequests.firstOrNull())
    }

    @Test
    fun `should serialize RepoDetails to GitHub-compatible JSON`() {
        val json = gitHubResponseMapper.writeValueAsString(repoDetailsOK)

        assert(json.contains("full_name"))
        assert(json.contains("clone_url"))
        assert(json.contains("stargazers_count"))
        assert(json.contains("created_at"))
    }

    @Test
    fun `should correctly deserialize full GitHub API response`() {
        val json = """
        {
            "full_name": "rkociniewski/prime",
            "description": "Sample repo",
            "clone_url": "https://github.com/rkociniewski/prime.git",
            "stargazers_count": 42,
            "created_at": "2023-03-06T22:16:48"
        }
        """.trimIndent()

        val result = gitHubResponseMapper.readValue(json, RepoDetails::class.java)

        assertEquals("rkociniewski/prime", result.fullName)
        assertEquals("Sample repo", result.description)
        assertEquals("https://github.com/rkociniewski/prime.git", result.cloneUrl)
        assertEquals(42, result.stars)
        assertEquals(LocalDateTime.of(2023, 3, 6, 22, 16, 48), result.createdAt)
    }

    @Test
    fun `should handle null values during deserialization`() {
        val result = gitHubResponseMapper.readValue(nullJson, RepoDetails::class.java)

        assertEquals("", result.fullName)
        assertEquals("", result.description)
        assertEquals("", result.cloneUrl)
        assertEquals(0, result.stars)
        assertEquals(LocalDateTime.MIN, result.createdAt)
    }

    @Test
    fun `should correctly serialize RepoDetails`() {

        val serialized = gitHubResponseMapper.writeValueAsString(repoDetailsTest)
            .replace(Regex("\\s"), "")

        assertEquals(repoDetailsTestJson, serialized)
    }

    @Test
    fun `should maintain symmetry in serialization-deserialization`() {
        val json = gitHubResponseMapper.writeValueAsString(repoDetailsSymmetryTest)
        val deserialized = gitHubResponseMapper.readValue(json, RepoDetails::class.java)

        assertEquals(repoDetailsSymmetryTest, deserialized)
    }

    @Test
    fun `should handle different date formats`() {
        val json = """{"created_at": "2023-03-06T22:16:48Z"}"""
        assertDoesNotThrow {
            gitHubResponseMapper.readValue(json, RepoDetails::class.java)
        }
    }

    @Test
    fun `should handle multiple date formats`() {
        val formats = listOf(
            "2023-03-06T22:16:48",
            "2023-03-06T22:16:48Z",
            "2023-03-06T22:16:48.000Z"
        )

        formats.forEach { dateStr ->
            val json = """{"created_at": "$dateStr"}"""
            assertDoesNotThrow {
                gitHubResponseMapper.readValue(json, RepoDetails::class.java)
            }
        }
    }

    @Test
    fun `should use default values for missing fields`() {
        val json = "{}"
        val result = gitHubResponseMapper.readValue(json, RepoDetails::class.java)

        assertEquals("", result.fullName)
        assertEquals("", result.description)
        assertEquals(0, result.stars)
        assertEquals(LocalDateTime.MIN, result.createdAt)
    }

    @Test
    fun `should fail to deserialize RepoDetails with invalid date format`() {

        val exception = assertFailsWith<InvalidFormatException> {
            gitHubResponseMapper.readValue(invalidDateJson, RepoDetails::class.java)
        }

        assert(exception.message?.contains("invalid-date") == true)
    }

    @Test
    fun `should deserialize date with timezone Z suffix`() {
        val result = gitHubResponseMapper.readValue(zonedDateTimeJson, RepoDetails::class.java)
        assertEquals(LocalDateTime.of(2023, 3, 6, 22, 16, 48), result.createdAt)
    }

    @Test
    fun `should throw exception for invalid stars type`() {
        assertFailsWith<MismatchedInputException> {
            gitHubResponseMapper.readValue(invalidStarsJson, RepoDetails::class.java)
        }
    }
}
