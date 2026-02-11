package co.ke.tinker.auth

import co.ke.tinker.config.Configuration
import co.ke.tinker.exception.AuthenticationException
import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.exception.NetworkException
import co.ke.tinker.http.HttpClient
import co.ke.tinker.http.HttpResponse
import co.ke.tinker.model.ApiMeta
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class AuthenticationManager(
    private val config: Configuration,
    private val httpClient: HttpClient
) {
    private var token: String? = null
    private var expiresAt: Long? = null
    var lastMeta: ApiMeta? = null
        private set

    fun getToken(): String {
        if (isTokenValid()) {
            return token!!
        }
        return fetchToken()
    }

    private fun isTokenValid(): Boolean {
        if (token == null || expiresAt == null) {
            return false
        }
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime < (expiresAt!! - 60)
    }

    private fun fetchToken(): String {
        try {
            val credentials = "${config.apiPublicKey}:${config.apiSecretKey}"
            val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray(StandardCharsets.UTF_8))

            val headers = mapOf(
                "Content-Type" to "application/x-www-form-urlencoded",
                "Accept" to "application/json"
            )
            val body = "credentials=${URLEncoder.encode(encodedCredentials, StandardCharsets.UTF_8.name())}"
            val response: HttpResponse = httpClient.post(config.authUrl, headers, body)
            val result = response.getJson()
            val authData = extractAuthData(result)

            if (response.statusCode >= 400) {
                throw AuthenticationException(extractErrorMessage(result), ExceptionCode.AUTHENTICATION_ERROR)
            }

            val tokenValue = authData["token"] as? String
            if (tokenValue == null) {
                throw NetworkException("Invalid authentication response: token missing", ExceptionCode.AUTHENTICATION_ERROR)
            }

            token = tokenValue
            val expiresIn = (authData["expires_in"] as? Number)?.toInt() ?: 3600
            expiresAt = (System.currentTimeMillis() / 1000) + expiresIn
            return token!!
        } catch (e: AuthenticationException) {
            throw e
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            throw NetworkException("Failed to authenticate: ${e.message}", ExceptionCode.AUTHENTICATION_ERROR, e)
        }
    }

    private fun extractAuthData(result: Map<String, Any?>): Map<String, Any?> {
        if (result.containsKey("success")) {
            lastMeta = ApiMeta.fromMap(result["meta"] as? Map<String, Any?>)
            if (result["success"] == false) {
                throw AuthenticationException(extractErrorMessage(result), ExceptionCode.AUTHENTICATION_ERROR)
            }
            return result["data"] as? Map<String, Any?> ?: emptyMap()
        }
        return result
    }

    private fun extractErrorMessage(result: Map<String, Any?>): String {
        val errorMap = result["error"] as? Map<String, Any?>
        if (errorMap != null) {
            return errorMap["message"] as? String ?: errorMap["code"] as? String ?: "Authentication failed"
        }
        return result["message"] as? String ?: "Authentication failed"
    }
}
