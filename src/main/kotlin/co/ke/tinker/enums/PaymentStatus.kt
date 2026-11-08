package co.ke.tinker.enums

enum class PaymentStatus(val value: String) {
    PENDING("pending"),
    AUTHORISED("authorised"),
    SUCCESS("success"),
    CANCELLED("cancelled"),
    FAILED("failed"),
    BLOCKED("blocked"),
    REFUNDED("refunded"),
    PARTIALLY_REFUNDED("partially_refunded"),
    EXPIRED("expired"),
    REVERSED("reversed"),
    UNKNOWN("unknown"),
    ;

    companion object {
        fun normalize(value: String?): String = when (value) {
            null -> "pending"
            "completed" -> "success"
            else -> value
        }

        fun fromString(value: String?): PaymentStatus {
            val normalized = normalize(value).lowercase()
            return entries.firstOrNull { it.value == normalized } ?: UNKNOWN
        }
    }

    override fun toString(): String = value
}
