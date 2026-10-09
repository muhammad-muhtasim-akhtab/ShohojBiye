package com.example.shohojbiye.domain.usecase

import javax.inject.Inject

class KabinCalculator @Inject constructor() {

    fun calculate(
        groomMonthlyIncome: Double,
        groomEducationLevel: Int, // 1-5
        brideEducationLevel: Int, // 1-5
        areaType: String // URBAN, RURAL
    ): KabinResult {
        // Simple logic for demonstration
        val baseAmount = groomMonthlyIncome * 12 // One year income as base
        val educationMultiplier = 1.0 + (groomEducationLevel + brideEducationLevel) * 0.1
        val areaMultiplier = if (areaType == "URBAN") 1.2 else 1.0

        val totalAmount = baseAmount * educationMultiplier * areaMultiplier
        
        val tier = when {
            totalAmount > 1000000 -> "PLATINUM"
            totalAmount > 500000 -> "GOLD"
            else -> "SILVER"
        }

        return KabinResult(
            amount = totalAmount,
            tier = tier,
            disclaimer = "This is a guidance estimator based on socio-economic factors. The final Kabin amount must be a mutual, negotiated agreement between both parties as per Islamic jurisprudence (Mahr)."
        )
    }

    data class KabinResult(
        val amount: Double,
        val tier: String,
        val disclaimer: String
    )
}
