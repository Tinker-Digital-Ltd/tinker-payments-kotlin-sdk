package co.ke.tinker.exception

class WebhookException(
    message: String,
    val code: Int = ExceptionCode.WEBHOOK_ERROR
) : RuntimeException(message)

