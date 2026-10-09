package com.example.shohojbiye.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shohojbiye.data.remote.NidVerificationService
import com.example.shohojbiye.data.remote.GovernmentRegistrySyncService
import com.example.shohojbiye.domain.usecase.FeeCalculator
import com.example.shohojbiye.data.repository.MarriageRepository
import com.example.shohojbiye.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val marriageRepository: MarriageRepository,
    private val sessionManager: SessionManager,
    private val feeCalculator: FeeCalculator,
    private val nidVerificationService: NidVerificationService,
    private val governmentRegistrySyncService: GovernmentRegistrySyncService
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState = _registrationState.asStateFlow()

    private val _kaziFee = MutableStateFlow(0.0)
    val kaziFee: StateFlow<Double> = _kaziFee.asStateFlow()

    fun onMahrChanged(amount: Double) {
        _kaziFee.value = feeCalculator.calculateRegistrationFee(amount)
    }

    fun register(
        groomId: String,
        brideId: String,
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
        vakilNid: String?
    ) {
        if (groomId.isBlank() || brideId.isBlank() || venue.isBlank() || 
            witness1Name.isBlank() || witness1Nid.isBlank() || 
            witness2Name.isBlank() || witness2Nid.isBlank()) {
            _registrationState.value = RegistrationState.Error("Please fill all required fields.")
            return
        }

        if (mahrTotal != mahrPrompt + mahrDeferred) {
            _registrationState.value = RegistrationState.Error("Total Mahr must be equal to Prompt + Deferred amount.")
            return
        }

        val kaziId = sessionManager.getUserId() ?: "unknown_kazi"
        val calculatedFee = feeCalculator.calculateRegistrationFee(mahrTotal)

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                // 1. Simulate EC Socket NID Verification with network delay
                val groomVerify = nidVerificationService.verifyNid(groomId)
                val brideVerify = nidVerificationService.verifyNid(brideId)
                if (groomVerify.isFailure || brideVerify.isFailure) {
                    _registrationState.value = RegistrationState.Error("NID verification failed against Election Commission database socket.")
                    return@launch
                }

                // 2. Simulate Central Government Registry Server Socket Sync (marriage.gov.bd)
                val syncResult = governmentRegistrySyncService.syncRegistration("TEMP-REG", "groom=$groomId,bride=$brideId")
                val syncStatus = syncResult.getOrNull()?.status ?: "PENDING_SYNC"

                val regId = marriageRepository.registerMarriage(
                    groomId = groomId,
                    brideId = brideId,
                    kaziId = kaziId,
                    date = date,
                    venue = venue,
                    mahrTotal = mahrTotal,
                    mahrPrompt = mahrPrompt,
                    mahrDeferred = mahrDeferred,
                    mahrPropertyDetails = mahrPropertyDetails,
                    witness1Name = witness1Name,
                    witness1Nid = witness1Nid,
                    witness2Name = witness2Name,
                    witness2Nid = witness2Nid,
                    vakilName = vakilName,
                    vakilNid = vakilNid,
                    kaziFee = calculatedFee
                )
                _registrationState.value = RegistrationState.Success(regId)
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Registration failed")
            }
        }
    }

    sealed class RegistrationState {
        data object Idle : RegistrationState()
        data object Loading : RegistrationState()
        data class Success(val regId: String) : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
}
