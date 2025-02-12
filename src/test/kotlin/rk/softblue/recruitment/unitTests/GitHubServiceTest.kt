package rk.softblue.recruitment.unitTests

import BaseUnitTest
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.plugins.NotFoundException
import rk.softblue.recruitment.testUtils.TestEntities.exampleRepoDetails
import rk.softblue.recruitment.testUtils.UnitTestBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubServiceTest : BaseUnitTest() {

    @Test
    fun `should fetch repository details successfully`() = UnitTestBuilder().build().withTest {
        val result = service.getRepoDetails("owner", "repository")
        assert(result.isSuccess)
        val repo = result.getOrThrow()
        assertEquals(exampleRepoDetails, repo)
        assertEquals("https://api.github.com/repos/owner/repository", capturedRequests.firstOrNull())
    }

    @Test
    fun `should handle empty response`() = UnitTestBuilder().emptyJson(true).build().withTest {
        val result = service.getRepoDetails("owner", "empty-repo")
        assert(result.isFailure)
        assert(result.exceptionOrNull() is JsonConvertException)
    }

    @Test
    fun `should handle NotFound`() =
        UnitTestBuilder().statusCode(HttpStatusCode.NotFound).build().withTest {
            val result = service.getRepoDetails("owner", "nonexistent-repo")
            assert(result.isFailure)
            assert(result.exceptionOrNull() is NotFoundException)
        }

    @Test
    fun `should handle Internal Server Error`() =
        UnitTestBuilder().statusCode(HttpStatusCode.InternalServerError).build().withTest {
            val result = service.getRepoDetails("owner", "repo")
            assert(result.isFailure)
            assert(result.exceptionOrNull() is ServerResponseException)
        }

    @Test
    fun `should handle Unauthorized (401)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.Unauthorized).build().withTest {
            val result = service.getRepoDetails("owner", "repo")
            assert(result.isFailure)
            assert(result.exceptionOrNull() is ClientRequestException)
        }

    @Test
    fun `should handle Forbidden (403)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.Forbidden).build().withTest {
            val result = service.getRepoDetails("owner", "repo")
            assert(result.isFailure)
            assert(result.exceptionOrNull() is ClientRequestException)
        }

    @Test
    fun `should fail on malformed JSON`() = UnitTestBuilder().invalidJson(true).build().withTest {
        val result = service.getRepoDetails("owner", "broken-repo")
        assert(result.isFailure)
        assert(result.exceptionOrNull() is JsonConvertException)
    }

    @Test
    fun `should fail on request timeout`() = UnitTestBuilder().delay(true).build().withTest {
        val result = service.getRepoDetails("owner", "timeout-repo")
        assert(result.isFailure)
        assert(result.exceptionOrNull() is HttpRequestTimeoutException)
    }
}
