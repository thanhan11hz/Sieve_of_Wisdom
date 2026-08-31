package com.example.sieve_of_wisdom.data.remote.interceptor

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

            return runBlocking {
                try {
                    val newTokens = authApiProvider.get().refreshToken(RefreshRequest(refreshToken))
                    authManager.saveTokens(newTokens.accessToken, newTokens.refreshToken);

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build();
                } catch (e: Exception) {
                    authManager.clear();
                    null;
                }
            }
        }
    }
}