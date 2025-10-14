package rk.powermilk.gris.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.time.LocalDateTime

/**
 * Data class representing GitHub repository details.
 * Uses Jackson annotations to properly deserialize JSON from GitHub API responses.
 *
 * @property fullName The full name of the repository (username/repo-name)
 * @property description The repository description
 * @property cloneUrl The URL that can be used to clone the repository
 * @property stars The number of stargazers (stars) the repository has
 * @property createdAt The date and time when the repository was created
 */
data class RepoDetails(
    @param:JsonProperty("full_name")
    @param:JsonSetter(nulls = Nulls.SKIP)
    val fullName: String = "",

    @param:JsonSetter(nulls = Nulls.SKIP)
    val description: String = "",

    @param:JsonProperty("clone_url")
    @param:JsonSetter(nulls = Nulls.SKIP)
    val cloneUrl: String = "",

    @param:JsonProperty("stargazers_count")
    @param:JsonSetter(nulls = Nulls.SKIP)
    val stars: Int = 0,

    @param:JsonProperty("created_at")
    @param:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @param:JsonSetter(nulls = Nulls.SKIP)
    val createdAt: LocalDateTime = LocalDateTime.MIN,
)
