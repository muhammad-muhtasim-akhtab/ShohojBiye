package com.example.shohojbiye.data.remote

import com.example.shohojbiye.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface ShohojBiyeApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("marriages/register")
    suspend fun registerMarriage(@Body request: MarriageRegistrationRequest): MarriageRegistrationResponse
}
