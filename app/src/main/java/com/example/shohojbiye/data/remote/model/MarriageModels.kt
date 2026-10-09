package com.example.shohojbiye.data.remote.model

data class MarriageRegistrationRequest(
    val groomId: String,
    val brideId: String,
    val kaziId: String,
    val date: Long,
    val venue: String,
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

data class MarriageRegistrationResponse(
    val registrationId: String,
    val regNumber: String,
    val qrCode: String,
    val status: String
)
