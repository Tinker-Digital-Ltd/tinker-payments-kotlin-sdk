package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefundResult(
    @JsonProperty("refund") val refund: Refund,
    @JsonProperty("payment") val payment: PaymentDetail,
    @JsonProperty("replayed") val replayed: Boolean,
)
