package co.ke.tinker.http

import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.exception.NetworkException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class HttpClient {
    private val client: OkHttpClient

    companion object {
        private const val TIMEOUT_SECONDS = 30L
    }

    init {
        client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    fun get(url: String, headers: Map<String, String>?): HttpResponse = send("GET", url, headers, null)

    fun post(url: String, headers: Map<String, String>?, body: String?): HttpResponse =
        send("POST", url, headers, body ?: "")

    fun put(url: String, headers: Map<String, String>?, body: String?): HttpResponse =
        send("PUT", url, headers, body ?: "")

    fun delete(url: String, headers: Map<String, String>?): HttpResponse = send("DELETE", url, headers, null)

    private fun send(method: String, url: String, headers: Map<String, String>?, body: String?): HttpResponse {
        return try {
            val requestBody = body?.toRequestBody(if (body.isEmpty()) null else "application/json".toMediaType())
            val requestBuilder = Request.Builder().url(url).method(method, requestBody)
            headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
            val response = client.newCall(requestBuilder.build()).execute()

            val responseHeaders = mutableMapOf<String, List<String>>()
            response.headers.names().forEach { name ->
                responseHeaders[name] = response.headers.values(name)
            }

            val responseBody = response.body?.string() ?: ""
            HttpResponse(response.code, responseBody, responseHeaders)
        } catch (e: Exception) {
            throw NetworkException("Network error: ${e.message}", ExceptionCode.NETWORK_ERROR, e)
        }
    }
}
