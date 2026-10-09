package com.example.shohojbiye.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registrations")
data class RegistrationEntity(
    @PrimaryKey val id: String,
    val groomId: String,
    val brideId: String,
    val kaziId: String,
    val date: Long,
    val venue: String,
    val status: String, // PENDING, APPROVED, REJECTED
    val regNumber: String?,
    val qrCode: String?,
    
    // Legal & Professional Fields
    val mahrTotal: Double,
    val mahrPrompt: Double,
    val mahrDeferred: Double,
    val mahrPropertyDetails: String?,
    
    val witness1Name: String,
    val witness1Nid: String,
    val witness2Name: String,
    val witness2Nid: String,
    
    val vakilName: String?,
    val vakilNid: String?,
    
    val kaziFee: Double
)
