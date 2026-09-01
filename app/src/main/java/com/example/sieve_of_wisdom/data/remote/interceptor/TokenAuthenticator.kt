package com.example.sieve_of_wisdom.data.remote.interceptor

import com.example.sieve_of_wisdom.data.remote.api.AuthApiService
import com.example.sieve_of_wisdom.data.remote.dto.RefreshTokenRequest
import com.example.sieve_of_wisdom.util.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager,
    private val authApiProvider: Provider<AuthApiService>
): Authenticator {
    private val lock = Any();

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(lock) {
            val currentToken = authManager.getAccessToken();
            val tokenUsedFailedRequest = response.request.header("Authorization")
                ?.removePrefix("Bearer ")

            if (currentToken != null && currentToken != tokenUsedFailedRequest) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build();
            }

            val refreshToken = authManager.getRefreshToken() ?: return null;

            return try {
                val retrofitResponse = authApiProvider.get().refreshToken(
                    RefreshTokenRequest(refreshToken)
                ).execute()

                val authResponse = retrofitResponse.body()

                if (retrofitResponse.isSuccessful && authResponse != null) {
                    authManager.saveTokens(authResponse.accessToken, authResponse.refreshToken);

                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${authResponse.accessToken}")
                        .build();
                } else {
                    authManager.clear();
                    null;
                }
            } catch (e: Exception) {
                authManager.clear();
                null;
            }
        }
    }
}