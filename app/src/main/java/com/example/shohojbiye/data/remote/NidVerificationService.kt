package com.example.shohojbiye.data.remote

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

data class NidVerificationResponse(
    val nid: String,
    val fullName: String,
    val dateOfBirth: String,
    val isValid: Boolean,
    val verificationSource: String = "MOCK_EC_SERVER_SOCKET"
)

interface NidVerificationService {
    suspend fun verifyNid(nid: String): Result<NidVerificationResponse>
}

@Singleton
class MockNidVerificationServiceImpl @Inject constructor() : NidVerificationService {
    override suspend fun verifyNid(nid: String): Result<NidVerificationResponse> {
        // Simulate external EC server network socket delay
        delay(500)
        return if (nid.length == 10 || nid.length == 17) {
            Result.success(
                NidVerificationResponse(
                    nid = nid,
                    fullName = "Citizen $nid",
                    dateOfBirth = "01/01/1995",
                    isValid = true
                )
            )
        } else {
            Result.failure(IllegalArgumentException("Invalid NID number according to Electoral Roll Database."))
        }
    }
}
