package co.ke.tinker.enums

enum class PaymentStatus(val value: String) {
    PENDING("pending"),
    SUCCESS("success"),
    CANCELLED("cancelled"),
    FAILED("failed");
    
    companion object {
        fun fromString(value: String?): PaymentStatus {
            if (value == null) {
                return PENDING
            }
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                PENDING
            }
        }
    }
    
    override fun toString(): String = value
}

