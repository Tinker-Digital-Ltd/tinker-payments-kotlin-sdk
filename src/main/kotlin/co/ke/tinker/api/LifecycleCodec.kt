package co.ke.tinker.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal object LifecycleCodec {
    val mapper = jacksonObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun payload(value: Any): Map<String, Any?> = mapper.convertValue(value)

    inline fun <reified T> decode(value: Map<String, Any?>): T = mapper.convertValue(value)

    fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20")

    fun payment(id: String): String = "merchant/payment/" + encode(id)

    fun headers(key: String): Map<String, String> {
        val normalized = key.trim()
        require(normalized.isNotEmpty() && normalized.toByteArray(StandardCharsets.UTF_8).size <= 255) {
            "Idempotency-Key must contain 1 to 255 UTF-8 bytes"
        }
        return mapOf("Idempotency-Key" to normalized)
    }

    fun positiveAmount(amount: Double) {
        require(amount.isFinite() && amount > 0) { "amount must be finite and positive" }
    }

    fun query(filters: Any): String {
        val query = payload(filters).entries.joinToString("&") {
            URLEncoder.encode(it.key, StandardCharsets.UTF_8) + "=" +
                URLEncoder.encode(it.value.toString(), StandardCharsets.UTF_8)
        }
        return if (query.isEmpty()) "" else "?$query"
    }
}
