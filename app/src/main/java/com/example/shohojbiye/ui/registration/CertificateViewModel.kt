package com.example.shohojbiye.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shohojbiye.data.local.entity.RegistrationEntity
import com.example.shohojbiye.data.repository.MarriageRepository
import com.example.shohojbiye.util.PdfGenerator
import com.example.shohojbiye.util.QRGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val marriageRepository: MarriageRepository,
    private val qrGenerator: QRGenerator,
    private val pdfGenerator: PdfGenerator
) : ViewModel() {

    private val _registration = MutableStateFlow<RegistrationEntity?>(null)
    val registration = _registration.asStateFlow()

    fun loadRegistration(regId: String) {
        viewModelScope.launch {
            _registration.value = marriageRepository.getRegistrationById(regId)
        }
    }

    fun generateQR(content: String) = qrGenerator.generateQRCode(content)

    fun generatePDF(regId: String, regNumber: String): File {
        return pdfGenerator.generateCertificate(regId, regNumber)
    }
}
