package co.ke.tinker.exception

class ApiException @JvmOverloads constructor(
    message: String,
    val code: Int = ExceptionCode.API_ERROR,
    val httpStatus: Int? = null,
    val details: Map<String, Any?>? = null,
    headers: Map<String, List<String>> = emptyMap(),
) : RuntimeException(message) {
    private val error = details?.get("error") as? Map<*, *> ?: emptyMap<Any, Any>()
    val errorCode: String? = error["code"] as? String
    val providerCode: String? = error["provider_code"] as? String
    val requestId: String? = (error["request_id"] as? String)?.takeIf { it.isNotEmpty() }
        ?: header(headers, "X-Request-ID")
    val retryAfter: String? = header(headers, "Retry-After")

    /** Only an explicit not_applied confirms no mutation. */
    val outcome: String? = error["outcome"] as? String

    private fun header(headers: Map<String, List<String>>, name: String): String? =
        headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value?.firstOrNull()
}
