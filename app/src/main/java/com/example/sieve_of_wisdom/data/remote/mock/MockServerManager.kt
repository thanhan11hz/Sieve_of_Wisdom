package com.example.sieve_of_wisdom.data.remote.mock

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockServerManager @Inject constructor() {

    private val mockWebServer = MockWebServer()

    fun startServer() {
        Thread {
            mockWebServer.dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return when (request.path) {
                        "/api/v1/auth/login" -> {
                            // Giả lập Response thành công cho Đăng nhập
                            MockResponse()
                                .setResponseCode(200)
                                .setBody(
                                    """
                                    {
                                        "access_token": "mock_access_token_xyz123",
                                        "refresh_token": "mock_refresh_token_abc456",
                                        "user": {
                                            "id": "u_1001",
                                            "username": "sangkhon_user",
                                            "email": "user@gmail.com",
                                            "coins": 500
                                        }
                                    }
                                    """.trimIndent()
                                )
                                .addHeader("Content-Type", "application/json")
                        }

                        "/api/v1/auth/refresh" -> {
                            // Giả lập Response cho Refresh Token
                            MockResponse()
                                .setResponseCode(200)
                                .setBody(
                                    """
                                    {
                                        "access_token": "mock_new_access_token_789",
                                        "refresh_token": "mock_new_refresh_token_012",
                                        "user": {
                                            "id": "u_1001",
                                            "username": "sangkhon_user",
                                            "email": "user@gmail.com",
                                            "coins": 500
                                        }
                                    }
                                    """.trimIndent()
                                )
                                .addHeader("Content-Type", "application/json")
                        }

                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }
            mockWebServer.start(8080) // Chạy server ở cổng 8080
        }.start()
    }

    fun getBaseUrl(): String {
        return mockWebServer.url("/").toString()
    }

    fun stopServer() {
        mockWebServer.shutdown()
    }
}