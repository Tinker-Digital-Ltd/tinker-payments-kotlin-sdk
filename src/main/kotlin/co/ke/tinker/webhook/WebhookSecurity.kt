package co.ke.tinker.webhook

data class WebhookSecurity(
    val signature: String = "",
    val algorithm: String = "HMAC-SHA256"
) {
    companion object {
        fun fromMap(security: Map<String, Any?>?): WebhookSecurity {
            if (security == null) {
                return WebhookSecurity()
            }
            return WebhookSecurity(
                signature = security["signature"] as? String ?: "",
                algorithm = security["algorithm"] as? String ?: "HMAC-SHA256"
            )
        }
    }
}

