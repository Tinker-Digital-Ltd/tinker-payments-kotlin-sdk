package co.ke.tinker.webhook

data class WebhookMeta(
    val version: String = "1.0",
    val appId: String = "",
    val gateway: String? = null
) {
    companion object {
        fun fromMap(meta: Map<String, Any?>?): WebhookMeta {
            if (meta == null) {
                return WebhookMeta()
            }
            return WebhookMeta(
                version = meta["version"] as? String ?: "1.0",
                appId = meta["app_id"] as? String ?: "",
                gateway = meta["gateway"] as? String
            )
        }
    }
}

