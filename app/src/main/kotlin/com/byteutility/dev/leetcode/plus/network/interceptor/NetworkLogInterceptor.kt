package com.byteutility.dev.leetcode.plus.network.interceptor

import com.byteutility.dev.leetcode.plus.network.cache.NetworkLogCache
import com.byteutility.dev.leetcode.plus.network.model.NetworkLog
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import javax.inject.Inject

class NetworkLogInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        val requestBody = request.body?.let { body ->
            try {
                val buffer = Buffer()
                body.writeTo(buffer)
                buffer.readUtf8()
            } catch (_: Exception) {
                null
            }
        }

        return try {
            val response = chain.proceed(request)
            val durationMs = System.currentTimeMillis() - startTime

            val responseBodyStr = try {
                response.peekBody(Long.MAX_VALUE).string()
            } catch (_: Exception) {
                null
            }

            NetworkLogCache.add(
                NetworkLog(
                    method = request.method,
                    url = request.url.toString(),
                    requestHeaders = request.headers.toFlatMap(),
                    requestBody = requestBody,
                    responseCode = response.code,
                    responseHeaders = response.headers.toFlatMap(),
                    responseBody = responseBodyStr,
                    durationMs = durationMs,
                    timestamp = startTime,
                )
            )

            response
        } catch (e: Exception) {
            NetworkLogCache.add(
                NetworkLog(
                    method = request.method,
                    url = request.url.toString(),
                    requestHeaders = request.headers.toFlatMap(),
                    requestBody = requestBody,
                    responseCode = -1,
                    responseHeaders = emptyMap(),
                    responseBody = null,
                    durationMs = System.currentTimeMillis() - startTime,
                    timestamp = startTime,
                    error = e.message,
                )
            )
            throw e
        }
    }

    private fun Headers.toFlatMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (i in 0 until size) {
            map[name(i)] = value(i)
        }
        return map
    }
}
