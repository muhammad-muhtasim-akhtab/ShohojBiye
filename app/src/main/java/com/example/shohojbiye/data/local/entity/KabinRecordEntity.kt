package com.example.shohojbiye.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kabin_records")
data class KabinRecordEntity(
    @PrimaryKey val registrationId: String,
    val tier: String,
    val amount: Double,
    val instalmentPlan: String?,
    val paymentMethod: String?,
    val riskFlag: Boolean
)
