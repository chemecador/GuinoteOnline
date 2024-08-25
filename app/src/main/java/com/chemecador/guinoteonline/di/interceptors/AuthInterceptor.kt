package com.chemecador.guinoteonline.di.interceptors

import com.chemecador.guinoteonline.data.local.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { userPreferences.authToken.firstOrNull() }

        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            runBlocking {
                userPreferences.clearAuthToken()
            }
        }

        return response
    }
}