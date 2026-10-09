package com.example.shohojbiye.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shohojbiye.data.repository.UserRepository
import com.example.shohojbiye.util.SessionManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private var verificationId: String? = null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.loginWithEmail(email, password)
            handleResult(result)
        }
    }

    fun signup(email: String, phone: String, password: String, role: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signupWithEmail(email, password, role, name, phone)
            if (result.isSuccess) {
                _authState.value = AuthState.SignupSuccess
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Signup failed")
            }
        }
    }

    // Phone Auth
    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        _authState.value = AuthState.Loading
        userRepository.startPhoneVerification(phoneNumber, activity, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                viewModelScope.launch {
                    val result = userRepository.signInWithPhoneCredential(credential)
                    handleResult(result)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                _authState.value = AuthState.CodeSent
            }
        })
    }

    fun signInWithCode(code: String) {
        val id = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(id, code)
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signInWithPhoneCredential(credential)
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<Unit>) {
        if (result.isSuccess) {
            val role = sessionManager.getUserRole() ?: "GENERAL_USER"
            _authState.value = AuthState.Authenticated(role)
        } else {
            _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Auth failed")
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        data object Idle : AuthState()
        data object Loading : AuthState()
        data object SignupSuccess : AuthState()
        data object CodeSent : AuthState()
        data class Authenticated(val role: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
