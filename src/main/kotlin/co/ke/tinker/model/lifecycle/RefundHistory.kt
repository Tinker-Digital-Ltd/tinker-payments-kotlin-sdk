package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefundHistory(
    @JsonProperty("refunded_amount") val refundedAmount: Double,
    @JsonProperty("refundable_amount") val refundableAmount: Double,
    @JsonProperty("refund_capability") val refundCapability: RefundCapability,
    @JsonProperty("refunds") val refunds: List<Refund>,
)
