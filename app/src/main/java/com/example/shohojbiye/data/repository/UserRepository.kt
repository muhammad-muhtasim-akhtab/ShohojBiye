package com.example.shohojbiye.data.repository

import android.app.Activity
import com.example.shohojbiye.data.local.dao.ShohojBiyeDao
import com.example.shohojbiye.data.local.entity.ProfileEntity
import com.example.shohojbiye.data.local.entity.UserEntity
import com.example.shohojbiye.util.SecurityUtils
import com.example.shohojbiye.util.SessionManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val dao: ShohojBiyeDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager,
    private val securityUtils: SecurityUtils
) {
    suspend fun loginWithEmail(email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            handleAuthSuccess(authResult.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signupWithEmail(email: String, password: String, role: String, name: String, phone: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Signup failed")
            
            val userData = mapOf(
                "email" to email,
                "phone" to phone,
                "role" to role,
                "name" to name,
                "createdAt" to System.currentTimeMillis()
            )
            
            firestore.collection("users").document(userId).set(userData).await()
            dao.insertUser(UserEntity(userId, phone, email, role))
            dao.insertProfile(ProfileEntity(userId, name, "", null, null, null, 0.0))
            
            handleAuthSuccess(authResult.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Phone Auth methods
    fun startPhoneVerification(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<Unit> {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            handleAuthSuccess(authResult.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun handleAuthSuccess(user: FirebaseUser?): Result<Unit> {
        val userId = user?.uid ?: return Result.failure(Exception("User not found"))
        
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val role = userDoc.getString("role") ?: "GENERAL_USER"
            val phone = userDoc.getString("phone") ?: user.phoneNumber ?: ""
            val email = userDoc.getString("email") ?: user.email ?: ""

            sessionManager.saveAuthToken(userId)
            sessionManager.saveUserRole(role)
            sessionManager.saveUserId(userId)
            
            dao.insertUser(UserEntity(userId, phone, email, role))
            Result.success(Unit)
        } catch (e: Exception) {
            // Even if Firestore fails, we have the Auth session. 
            // In a real app, you might want to force a profile setup.
            Result.success(Unit) 
        }
    }

    @Suppress("unused")
    suspend fun getUser(id: String): UserEntity? = dao.getUserById(id)

    @Suppress("unused")
    suspend fun getProfile(userId: String): ProfileEntity? = dao.getProfileByUserId(userId)
}
