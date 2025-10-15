package rk.powermilk.gris.unitTests

import BaseUnitTest
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import rk.powermilk.gris.di.notFoundException
import rk.powermilk.gris.testUtils.TestEntities.exampleRepoDetails
import rk.powermilk.gris.testUtils.UnitTestBuilder
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GitHubServiceTest : BaseUnitTest() {

    @Test
    fun `should fetch repository details successfully`() = UnitTestBuilder().build().withTest {
        val result = service.getRepoDetails("owner", "repository")
        assertTrue { result.isSuccess }
        val repo = result.getOrThrow()
        assertEquals(exampleRepoDetails, repo)
        assertEquals("https://api.github.com/repos/owner/repository", capturedRequests.firstOrNull())
    }

    @Test
    fun `should throw JsonConvertException for empty response`() = UnitTestBuilder().emptyJson(true).build().withTest {
        assertFailsWith<JsonConvertException> { service.getRepoDetails("owner", "empty-repo") }
    }

    @Test
    fun `should fail on malformed JSON`() = UnitTestBuilder().invalidJson(true).build().withTest {
        val exception = assertFailsWith<JsonConvertException> {
            service.getRepoDetails("owner", "broken-repo").exceptionOrNull()
        }

        assertContains(exception.message ?: "", "Unexpected end-of-input")
    }

    @Test
    fun `should fail on request timeout`() = UnitTestBuilder().delay(true).build().withTest {
        assertFailsWith<HttpRequestTimeoutException> {
            service.getRepoDetails("owner", "timeout-repo")
        }
    }

    @Test
    fun `should handle Unauthorized (401)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.Unauthorized).build().withTest {
            val result = service.getRepoDetails("owner", "unauthorized")

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is RuntimeException)
            assertEquals("GitHub API error: 401 Unauthorized", exception.message)
        }


    @Test
    fun `should handle Forbidden (403)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.Forbidden).build().withTest {
            val result = service.getRepoDetails("owner", "forbidden")

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is BadRequestException)
            assertEquals("GitHub API rate limit exceeded", exception.message)
        }

    @Test
    fun `should handle Not Found (404)`() {
        val status = HttpStatusCode.NotFound
        UnitTestBuilder().statusCode(status).build().withTest {
            val exception = assertFailsWith<NotFoundException> {
                service.getRepoDetails("owner", "not-foud")
            }

            assertTrue { exception.message!!.contains(notFoundException.message.toString()) }
        }
    }

    @Test
    fun `should return failure for TooManyRequests (429)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.TooManyRequests).build().withTest {
            val result = service.getRepoDetails("owner", "too-many-requests")

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is BadRequestException)
            assertEquals("GitHub API rate limit exceeded", exception.message)
        }

    @Test
    fun `should handle Internal Server Error (500)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.InternalServerError).build().withTest {
            val result = service.getRepoDetails("owner", "internal-server-error")

            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is RuntimeException)
            assertEquals("GitHub API error: 500 Internal Server Error", exception.message)
        }

    @Test
    fun `should return failure for BadGateway (502)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.BadGateway).build().withTest {
            val result = service.getRepoDetails("owner", "repo")

            assertTrue(result.isFailure)

            val exception = result.exceptionOrNull()
            assertTrue(exception is RuntimeException)
            assertEquals(exception.message?.contains("GitHub API error: 502"), true)
        }
}
