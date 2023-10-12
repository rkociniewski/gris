package rk.softblue.recruitment.e2eTests

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import rk.softblue.recruitment.TestEntities
import rk.softblue.recruitment.model.JsonMapper.defaultMapper
import kotlin.test.Test

class GithubControllerTest : BaseUnitTest() {
    @Test
    fun `Should return 'Hello world!' for ping endpoint`() = withTest {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun `Should return 404 for non-existing endpoint`() = withTest {
        val response = client.get("/doesnt_exist")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(defaultMapper.writeValueAsString(TestEntities.endpointNotFoundResponse), response.bodyAsText())
    }

    @Test
    fun `Should return repo details when params are correct`() = withTest {
        val response = client.get("/repositories/rkociniewski/quiz")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(defaultMapper.writeValueAsString(TestEntities.repoDetailsOK), response.bodyAsText())
    }

    @Test
    fun `Should return repo details when owner is incorrect`() = withTest {
        val response = client.get("/repositories/rkociniewski1/quiz")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(defaultMapper.writeValueAsString(TestEntities.repoDetailsNotFound), response.bodyAsText())
    }

    @Test
    fun `Should return repo details when repo name is incorrect`() = withTest {
        val response = client.get("/repositories/rkociniewski/quiz1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(defaultMapper.writeValueAsString(TestEntities.repoDetailsNotFound), response.bodyAsText())
    }
}