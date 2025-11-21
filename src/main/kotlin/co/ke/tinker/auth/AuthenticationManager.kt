package co.ke.tinker.auth

import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.exception.AuthenticationException
import co.ke.tinker.exception.NetworkException
import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.http.HttpClient
import co.ke.tinker.http.HttpResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class AuthenticationManager(
    private val config: Configuration,
    private val httpClient: HttpClient
) {
    private var token: String? = null
    private var expiresAt: Long? = null

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
            val encodedCredentials = Base64.getEncoder().encodeToString(
                credentials.toByteArray(StandardCharsets.UTF_8)
            )

            val url = Endpoints.AUTH_TOKEN_URL
            val headers = mapOf(
                "Content-Type" to "application/x-www-form-urlencoded",
                "Accept" to "application/json"
            )

            val body = "credentials=${URLEncoder.encode(encodedCredentials, StandardCharsets.UTF_8.name())}"

            val response: HttpResponse = httpClient.post(url, headers, body)
            val result = response.getJson()

            if (response.statusCode >= 400) {
                val message = result["message"] as? String ?: "Authentication failed"
                throw AuthenticationException(message, ExceptionCode.AUTHENTICATION_ERROR)
            }

            val tokenValue = result["token"] as? String
            if (tokenValue == null) {
                throw NetworkException(
                    "Invalid authentication response: token missing",
                    ExceptionCode.AUTHENTICATION_ERROR
                )
            }

            this.token = tokenValue
            val expiresInObj = result["expires_in"]
            val expiresIn = (expiresInObj as? Number)?.toInt() ?: 3600
            this.expiresAt = (System.currentTimeMillis() / 1000) + expiresIn

            return this.token!!
        } catch (e: AuthenticationException) {
            throw e
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            throw NetworkException(
                "Failed to authenticate: ${e.message}",
                ExceptionCode.AUTHENTICATION_ERROR,
                e
            )
        }
    }
}


