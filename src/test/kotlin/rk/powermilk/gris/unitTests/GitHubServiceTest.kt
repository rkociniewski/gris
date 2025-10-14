package rk.powermilk.gris.unitTests

import BaseUnitTest
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
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
    fun `should handle NotFound`() =
        UnitTestBuilder().statusCode(HttpStatusCode.NotFound).build().withTest {
            // Assertion of throwable
            assertFailsWith<NotFoundException>(
                notFoundException.message,
                { service.getRepoDetails("owner", "nonexistent-repo") }
            )
        }

    @Test
    fun `should handle Internal Server Error`() =
        UnitTestBuilder().statusCode(HttpStatusCode.InternalServerError).build().withTest {
            val exception = assertFailsWith<ServerResponseException> {
                service.getRepoDetails("owner", "repo")
            }
            assertEquals(HttpStatusCode.InternalServerError, exception.response.status)
        }

    @Test
    fun `should handle Unauthorized (401)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.Unauthorized).build().withTest {
            val exception = assertFailsWith<ClientRequestException> {
                service.getRepoDetails("owner", "repo")
            }
            assertEquals(HttpStatusCode.Unauthorized, exception.response.status)
        }

    @Test
    fun `should handle Forbidden (403)`() =
        UnitTestBuilder().statusCode(HttpStatusCode.Forbidden).build().withTest {
            val exception = assertFailsWith<ClientRequestException> {
                service.getRepoDetails("owner", "repo")
            }
            assertEquals(HttpStatusCode.Forbidden, exception.response.status)
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
}
