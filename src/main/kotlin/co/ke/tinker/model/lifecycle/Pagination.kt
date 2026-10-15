package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Pagination(
    @JsonProperty("page") val page: Int,
    @JsonProperty("per_page") val perPage: Int,
    @JsonProperty("total") val total: Int,
    @JsonProperty("total_pages") val totalPages: Int,
)
