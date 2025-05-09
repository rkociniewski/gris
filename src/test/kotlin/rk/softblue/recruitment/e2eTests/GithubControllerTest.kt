package rk.softblue.recruitment.e2eTests

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import rk.softblue.recruitment.model.JsonMapper.gitHubResponseMapper
import rk.softblue.recruitment.testUtils.TestEntities
import rk.softblue.recruitment.testUtils.TestEntities.repoDetailsNotFound
import kotlin.test.Test
import kotlin.test.assertEquals

class GithubControllerTest : BaseE2ETest() {
    @Test
    fun `Should return 'Hello world!' for ping endpoint`() = withTest(HttpStatusCode.OK) {
        val response = client.get("/") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun `Should return 404 for non-existing endpoint`() = withTest(HttpStatusCode.NotFound) {
        val response = client.get("/doesnt_exist") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            gitHubResponseMapper.writeValueAsString(TestEntities.endpointNotFoundResponse),
            response.bodyAsText()
        )
    }

    @Test
    fun `Should return repo details when params are correct`() = withTest(HttpStatusCode.OK) {
        val response = client.get("/repositories/rkociniewski/prime") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(gitHubResponseMapper.writeValueAsString(TestEntities.repoDetailsOK), response.bodyAsText())
    }

    @Test
    fun `Should return repo details not found when owner is incorrect`() = withTest(HttpStatusCode.NotFound) {
        val response = client.get("/repositories/rkociniewski1/prime") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(gitHubResponseMapper.writeValueAsString(TestEntities.repoDetailsNotFound), response.bodyAsText())
    }

    @Test
    fun `Should return error when repo not found`() = withTest(HttpStatusCode.NotFound) {
        val response = client.get("/repositories/rkociniewski/prime1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(
            gitHubResponseMapper.writeValueAsString(repoDetailsNotFound),
            response.bodyAsText()
        )
    }
}
