package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonProperty

enum class RefundReason {
    @JsonProperty("requested_by_customer")
    REQUESTED_BY_CUSTOMER,

    @JsonProperty("duplicate")
    DUPLICATE,

    @JsonProperty("fraudulent")
    FRAUDULENT,

    @JsonProperty("other")
    OTHER,
}
