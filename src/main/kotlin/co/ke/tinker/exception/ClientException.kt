package co.ke.tinker.exception

class ClientException(
    message: String,
    val code: Int = ExceptionCode.CLIENT_ERROR
) : RuntimeException(message)

