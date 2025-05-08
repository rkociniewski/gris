package rk.softblue.recruitment.model

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonSetter
import java.time.LocalDateTime

data class RepoDetails(
    @JsonSetter("full_name")
    @get:JsonGetter("fullName")
    val fullName: String? = "",
    val description: String? = "",
    @JsonSetter("clone_url")
    @get:JsonGetter("cloneUrl")
    val cloneUrl: String? = "",
    @JsonSetter("stargazers_count")
    @get:JsonGetter("stars")
    val stars: Int? = 0,
    @JsonSetter("created_at")
    @get:JsonGetter("createdAt")
    val createdAt: LocalDateTime? = LocalDateTime.MIN,
)
