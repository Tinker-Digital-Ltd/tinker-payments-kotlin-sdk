package co.ke.tinker.exception

class ApiException(
    message: String,
    val code: Int = ExceptionCode.API_ERROR
) : RuntimeException(message)

