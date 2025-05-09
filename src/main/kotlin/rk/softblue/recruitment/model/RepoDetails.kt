package rk.softblue.recruitment.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.time.LocalDateTime

data class RepoDetails(
    @JsonProperty("full_name")
    @JsonSetter(nulls = Nulls.SKIP)
    val fullName: String = "",

    @JsonSetter(nulls = Nulls.SKIP)
    val description: String = "",

    @JsonProperty("clone_url")
    @JsonSetter(nulls = Nulls.SKIP)
    val cloneUrl: String = "",

    @JsonProperty("stargazers_count")
    @JsonSetter(nulls = Nulls.SKIP)
    val stars: Int = 0,

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSetter(nulls = Nulls.SKIP)
    val createdAt: LocalDateTime = LocalDateTime.MIN,
)
