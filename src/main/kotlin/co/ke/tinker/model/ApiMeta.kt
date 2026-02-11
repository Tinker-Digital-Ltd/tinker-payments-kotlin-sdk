package co.ke.tinker.model

data class ApiMeta(
    val requestId: String? = null,
    val timestamp: String? = null,
    val environment: String? = null
) {
    companion object {
        fun fromMap(meta: Map<String, Any?>?): ApiMeta {
            return ApiMeta(
                requestId = meta?.get("request_id") as? String,
                timestamp = meta?.get("timestamp") as? String,
                environment = meta?.get("environment") as? String
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "request_id" to requestId,
        "timestamp" to timestamp,
        "environment" to environment
    )
}
