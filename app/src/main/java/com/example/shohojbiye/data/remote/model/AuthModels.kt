package com.example.shohojbiye.data.remote.model

data class LoginRequest(
    val email: String,
    val phone: String,
    val password: String,
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val role: String
)

data class RegisterRequest(
    val email: String,
    val phone: String,
    val password: String,
    val role: String,
    val name: String
)

data class RegisterResponse(
    val userId: String,
    val success: Boolean
)
