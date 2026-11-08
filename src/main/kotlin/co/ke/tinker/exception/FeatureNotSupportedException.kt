package co.ke.tinker.exception

class FeatureNotSupportedException(
    message: String,
    httpStatus: Int?,
    details: Map<String, Any?>?,
    headers: Map<String, List<String>> = emptyMap(),
) : ApiException(message, ExceptionCode.API_ERROR, httpStatus, details, headers) {
    private val info = ((details?.get("error") as? Map<*, *>)?.get("details") as? Map<*, *>) ?: emptyMap<Any, Any>()
    val gateway: String? = info["gateway"] as? String
    val feature: String? = info["feature"] as? String
    val alternatives: List<String> = (info["alternatives"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
}
