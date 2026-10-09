package com.example.shohojbiye.data.repository

import com.example.shohojbiye.data.local.dao.ShohojBiyeDao
import com.example.shohojbiye.data.local.entity.UserEntity
import com.example.shohojbiye.util.SecurityUtils
import com.example.shohojbiye.util.SessionManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private val dao = mockk<ShohojBiyeDao>(relaxed = true)
    private val auth = mockk<FirebaseAuth>()
    private val firestore = mockk<FirebaseFirestore>()
    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val securityUtils = mockk<SecurityUtils>(relaxed = true)

    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = UserRepository(dao, auth, firestore, sessionManager, securityUtils)
    }

    @Test
    fun loginWithEmail_success_updatesSessionAndDao() = runTest {
        val email = "test@example.com"
        val password = "password"
        val userId = "user123"
        
        val mockAuthResult = mockk<AuthResult>()
        val mockUser = mockk<FirebaseUser>()
        every { mockAuthResult.user } returns mockUser
        every { mockUser.uid } returns userId
        
        every { auth.signInWithEmailAndPassword(email, password) } returns Tasks.forResult(mockAuthResult)
            
        val mockDoc = mockk<DocumentSnapshot>()
        every { mockDoc.getString("role") } returns "KAZI"
        every { mockDoc.getString("phone") } returns "123456789"
        every { mockDoc.getString("email") } returns email
        every { mockUser.phoneNumber } returns "123456789"
        every { mockUser.email } returns email
        
        val mockCollection = mockk<CollectionReference>()
        val mockDocument = mockk<DocumentReference>()
        every { firestore.collection("users") } returns mockCollection
        every { mockCollection.document(userId) } returns mockDocument
        every { mockDocument.get() } returns Tasks.forResult(mockDoc)

        val result = userRepository.loginWithEmail(email, password)

        assertTrue(result.isSuccess)
        verify { sessionManager.saveUserId(userId) }
        verify { sessionManager.saveUserRole("KAZI") }
        coVerify { dao.insertUser(any()) }
    }
}
