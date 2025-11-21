package co.ke.tinker.exception

class NetworkException(
    message: String,
    val code: Int = ExceptionCode.NETWORK_ERROR,
    cause: Throwable? = null
) : RuntimeException(message, cause)

