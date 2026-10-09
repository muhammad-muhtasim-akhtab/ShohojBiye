package com.example.shohojbiye.data.repository

import com.example.shohojbiye.data.local.dao.ShohojBiyeDao
import com.example.shohojbiye.data.local.entity.RegistrationEntity
import com.example.shohojbiye.util.SecurityUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarriageRepository @Inject constructor(
    private val dao: ShohojBiyeDao,
    private val firestore: FirebaseFirestore,
    private val securityUtils: SecurityUtils
) {
    fun getAllRegistrations(): Flow<List<RegistrationEntity>> = dao.getAllRegistrations()

    suspend fun getRegistrationById(id: String): RegistrationEntity? {
        val entity = dao.getRegistrationById(id) ?: return null
        return entity.copy(
            groomId = try { securityUtils.decrypt(entity.groomId) } catch (e: Exception) { entity.groomId },
            brideId = try { securityUtils.decrypt(entity.brideId) } catch (e: Exception) { entity.brideId },
            witness1Nid = try { securityUtils.decrypt(entity.witness1Nid) } catch (e: Exception) { entity.witness1Nid },
            witness2Nid = try { securityUtils.decrypt(entity.witness2Nid) } catch (e: Exception) { entity.witness2Nid },
            vakilNid = entity.vakilNid?.let { try { securityUtils.decrypt(it) } catch (e: Exception) { it } }
        )
    }

    suspend fun registerMarriage(
        groomId: String,
        brideId: String,
        kaziId: String,
        date: Long,
        venue: String,
        mahrTotal: Double,
        mahrPrompt: Double,
        mahrDeferred: Double,
        mahrPropertyDetails: String?,
        witness1Name: String,
        witness1Nid: String,
        witness2Name: String,
        witness2Nid: String,
        vakilName: String?,
        vakilNid: String?,
        kaziFee: Double
    ): String {
        val registrationId = UUID.randomUUID().toString()
        val regNumber = "SB-${Calendar.getInstance().get(Calendar.YEAR)}-${(1000..9999).random()}"
        
        // Encrypt sensitive IDs for both local and cloud storage (optional for cloud, but safer)
        val encryptedGroomNid = securityUtils.encrypt(groomId)
        val encryptedBrideNid = securityUtils.encrypt(brideId)
        val encryptedW1Nid = securityUtils.encrypt(witness1Nid)
        val encryptedW2Nid = securityUtils.encrypt(witness2Nid)
        val encryptedVakilNid = vakilNid?.let { securityUtils.encrypt(it) }

        val registrationData = mapOf(
            "id" to registrationId,
            "regNumber" to regNumber,
            "groomId" to encryptedGroomNid,
            "brideId" to encryptedBrideNid,
            "kaziId" to kaziId,
            "date" to date,
            "venue" to venue,
            "status" to "REGISTERED",
            "mahrTotal" to mahrTotal,
            "mahrPrompt" to mahrPrompt,
            "mahrDeferred" to mahrDeferred,
            "mahrPropertyDetails" to mahrPropertyDetails,
            "witness1Name" to witness1Name,
            "witness1Nid" to encryptedW1Nid,
            "witness2Name" to witness2Name,
            "witness2Nid" to encryptedW2Nid,
            "vakilName" to vakilName,
            "vakilNid" to encryptedVakilNid,
            "kaziFee" to kaziFee,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("registrations").document(registrationId).set(registrationData).await()

        dao.insertRegistration(
            RegistrationEntity(
                id = registrationId,
                groomId = encryptedGroomNid,
                brideId = encryptedBrideNid,
                kaziId = kaziId,
                date = date,
                venue = venue,
                status = "REGISTERED",
                regNumber = regNumber,
                qrCode = "https://shohojbiye.com/verify/$registrationId",
                mahrTotal = mahrTotal,
                mahrPrompt = mahrPrompt,
                mahrDeferred = mahrDeferred,
                mahrPropertyDetails = mahrPropertyDetails,
                witness1Name = witness1Name,
                witness1Nid = encryptedW1Nid,
                witness2Name = witness2Name,
                witness2Nid = encryptedW2Nid,
                vakilName = vakilName,
                vakilNid = encryptedVakilNid,
                kaziFee = kaziFee
            )
        )
        return registrationId
    }
}
