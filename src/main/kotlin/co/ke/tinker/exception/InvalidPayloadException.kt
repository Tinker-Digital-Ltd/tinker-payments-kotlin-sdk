package co.ke.tinker.exception

class InvalidPayloadException(
    message: String,
    val code: Int = ExceptionCode.INVALID_PAYLOAD
) : RuntimeException(message)

