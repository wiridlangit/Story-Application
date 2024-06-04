package com.dicoding.picodiploma.loginwithanimation.data.api

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)
