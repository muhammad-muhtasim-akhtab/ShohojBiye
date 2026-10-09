package com.example.shohojbiye.data.repository

import com.example.shohojbiye.data.local.dao.ShohojBiyeDao
import com.example.shohojbiye.util.SecurityUtils
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarriageRepositoryTest {

    private val dao = mockk<ShohojBiyeDao>(relaxed = true)
    private val firestore = mockk<FirebaseFirestore>()
    private val securityUtils = mockk<SecurityUtils>(relaxed = true)

    private lateinit var marriageRepository: MarriageRepository

    @Before
    fun setUp() {
        marriageRepository = MarriageRepository(dao, firestore, securityUtils)
    }

    @Test
    fun registerMarriage_success_savesToFirestoreAndDao() = runTest {
        val groomId = "groom123"
        val brideId = "bride123"
        val mockCollection = mockk<CollectionReference>()
        val mockDocument = mockk<DocumentReference>()
        
        every { firestore.collection("registrations") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockDocument.set(any()) } returns Tasks.forResult(null)

        val regId = marriageRepository.registerMarriage(
            groomId = groomId,
            brideId = brideId,
            kaziId = "kazi123",
            date = System.currentTimeMillis(),
            venue = "Test Venue",
            mahrTotal = 100000.0,
            mahrPrompt = 50000.0,
            mahrDeferred = 50000.0,
            mahrPropertyDetails = null,
            witness1Name = "W1",
            witness1Nid = "1234567890",
            witness2Name = "W2",
            witness2Nid = "0987654321",
            vakilName = null,
            vakilNid = null,
            kaziFee = 1250.0
        )

        assertNotNull(regId)
        coVerify { mockDocument.set(any()) }
        coVerify { dao.insertRegistration(any()) }
    }
}
