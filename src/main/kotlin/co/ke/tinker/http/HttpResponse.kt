package co.ke.tinker.http

import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.exception.ExceptionCode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

data class HttpResponse(
    val statusCode: Int,
    val body: String,
    val headers: Map<String, List<String>>
) {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    
    fun getJson(): Map<String, Any?> {
        return try {
            objectMapper.readValue<Map<String, Any?>>(body)
        } catch (e: Exception) {
            throw InvalidPayloadException("Invalid JSON response: ${e.message}", ExceptionCode.INVALID_PAYLOAD)
        }
    }
}

