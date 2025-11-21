package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.exception.ApiException
import co.ke.tinker.exception.NetworkException
import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.http.HttpClient
import co.ke.tinker.http.HttpResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

abstract class BaseManager(
    protected val config: Configuration,
    protected val httpClient: HttpClient,
    protected val authManager: AuthenticationManager
) {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    protected fun request(method: String, endpoint: String, data: Map<String, Any?>?): Map<String, Any?> {
        var baseUrl = config.baseUrl.removeSuffix("/")
        var endpointPath = endpoint.removePrefix("/")
        val url = "$baseUrl/$endpointPath"

        val token = authManager.getToken()
        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Accept" to "application/json",
            "Content-Type" to "application/json"
        )

        val body = if (data != null && data.isNotEmpty()) {
            try {
                objectMapper.writeValueAsString(data)
            } catch (e: Exception) {
                throw NetworkException(
                    "Failed to serialize request data: ${e.message}",
                    ExceptionCode.NETWORK_ERROR,
                    e
                )
            }
        } else {
            null
        }

        val response: HttpResponse = httpClient.post(url, headers, body)
        val result = response.getJson()

        if (response.statusCode >= 400) {
            val message = when {
                result is Map<*, *> && result.containsKey("message") -> result["message"] as? String
                result is Map<*, *> && result.containsKey("error") -> result["error"] as? String
                else -> "Unknown error"
            } ?: "Unknown error"
            throw ApiException(message, ExceptionCode.API_ERROR)
        }

        return result ?: emptyMap()
    }
}


