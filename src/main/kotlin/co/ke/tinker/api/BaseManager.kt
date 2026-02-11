package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.exception.ApiException
import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.exception.NetworkException
import co.ke.tinker.http.HttpClient
import co.ke.tinker.http.HttpResponse
import co.ke.tinker.model.ApiMeta
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

abstract class BaseManager(
    protected val config: Configuration,
    protected val httpClient: HttpClient,
    protected val authManager: AuthenticationManager
) {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    var lastMeta: ApiMeta? = null
        protected set

    protected fun request(method: String, endpoint: String, data: Map<String, Any?>?): Map<String, Any?> {
        val baseUrl = config.baseUrl.removeSuffix("/")
        val endpointPath = endpoint.removePrefix("/")
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
                throw NetworkException("Failed to serialize request data: ${e.message}", ExceptionCode.NETWORK_ERROR, e)
            }
        } else null

        val response: HttpResponse = if (method.uppercase() == "GET") {
            httpClient.get(url, headers)
        } else {
            httpClient.post(url, headers, body)
        }
        val result = response.getJson()

        if (response.statusCode >= 400) {
            throw ApiException(extractErrorMessage(result), ExceptionCode.API_ERROR)
        }

        if (result.containsKey("success")) {
            lastMeta = ApiMeta.fromMap(result["meta"] as? Map<String, Any?>)
            if (result["success"] == false) {
                throw ApiException(extractErrorMessage(result), ExceptionCode.API_ERROR)
            }

            val dataObj = result["data"]
            return when (dataObj) {
                is Map<*, *> -> dataObj as Map<String, Any?>
                else -> mapOf("value" to dataObj)
            }
        }

        return result
    }

    protected fun extractErrorMessage(result: Map<String, Any?>): String {
        val errorMap = result["error"] as? Map<String, Any?>
        if (errorMap != null) {
            return errorMap["message"] as? String ?: errorMap["code"] as? String ?: "Unknown error"
        }
        return result["message"] as? String ?: "Unknown error"
    }
}
