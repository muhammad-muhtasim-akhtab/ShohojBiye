package com.example.shohojbiye.util

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class DigitalSignatureMetadata(
    val signedBy: String,
    val serialNumber: String,
    val timestamp: String,
    val cryptographicHash: String,
    val watermarkText: String
)

@Singleton
class DigitalSignatureService @Inject constructor() {

    fun signDocument(regId: String, regNumber: String, kaziName: String = "Authorized Nikah Registrar"): DigitalSignatureMetadata {
        val timestampStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault()).format(Date())
        val rawData = "$regId:$regNumber:$kaziName:$timestampStr"
        
        // Calculate SHA-256 cryptographic hash simulating PKI signing
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawData.toByteArray(Charsets.UTF_8))
        val hashHex = hashBytes.joinToString("") { "%02x".format(it) }

        return DigitalSignatureMetadata(
            signedBy = "$kaziName (MOCK PKI X.509)",
            serialNumber = "PKI-SIG-${regNumber.takeLast(6)}-2026",
            timestamp = timestampStr,
            cryptographicHash = hashHex,
            watermarkText = "DUMMY PRACTICE COPY - NOT OFFICIAL GOVT GAZETTE [MOCK VENDOR SOCKET]"
        )
    }
}
