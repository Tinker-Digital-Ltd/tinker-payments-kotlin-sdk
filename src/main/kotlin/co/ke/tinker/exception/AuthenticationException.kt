package co.ke.tinker.exception

class AuthenticationException(
    message: String,
    val code: Int = ExceptionCode.AUTHENTICATION_ERROR
) : RuntimeException(message)

