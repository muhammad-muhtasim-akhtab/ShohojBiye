package com.example.shohojbiye.data.remote

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

data class SyncResult(
    val trackingId: String,
    val status: String, // "SYNCED_TO_CENTRAL_REGISTRY", "PENDING", "FAILED"
    val serverTimestamp: Long
)

interface GovernmentRegistrySyncService {
    suspend fun syncRegistration(regId: String, regDataPayload: String): Result<SyncResult>
}

@Singleton
class MockGovernmentRegistrySyncServiceImpl @Inject constructor() : GovernmentRegistrySyncService {
    override suspend fun syncRegistration(regId: String, regDataPayload: String): Result<SyncResult> {
        // Simulate central server socket sync
        delay(400)
        return Result.success(
            SyncResult(
                trackingId = "GOV-SYNC-2026-${regId.takeLast(6)}",
                status = "SYNCED_TO_CENTRAL_REGISTRY",
                serverTimestamp = System.currentTimeMillis()
            )
        )
    }
}
