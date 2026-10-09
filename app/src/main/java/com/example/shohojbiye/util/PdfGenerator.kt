package com.example.shohojbiye.util

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val digitalSignatureService: DigitalSignatureService
) {

    fun generateCertificate(regId: String, regNumber: String): File {
        val file = File(context.cacheDir, "Kabinnama_$regId.pdf")
        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        val signatureMeta = digitalSignatureService.signDocument(regId, regNumber)

        document.add(Paragraph("*** DUMMY PRACTICE COPY ***").setFontSize(14f))
        document.add(Paragraph("Shohoj Biye - Kazi Practice Management Portal").setFontSize(22f))
        document.add(Paragraph("Registration Number: $regNumber"))
        document.add(Paragraph("Registration ID: $regId"))
        document.add(Paragraph("Date: ${System.currentTimeMillis()}"))
        document.add(Paragraph("\n--- MOCK PKI DIGITAL SIGNATURE & VERIFICATION ---"))
        document.add(Paragraph("Signed By: ${signatureMeta.signedBy}"))
        document.add(Paragraph("PKI Serial: ${signatureMeta.serialNumber}"))
        document.add(Paragraph("Timestamp: ${signatureMeta.timestamp}"))
        document.add(Paragraph("Cryptographic SHA-256 Hash: ${signatureMeta.cryptographicHash}"))
        document.add(Paragraph("\n[NOTICE]: ${signatureMeta.watermarkText}"))

        document.close()
        return file
    }
}
