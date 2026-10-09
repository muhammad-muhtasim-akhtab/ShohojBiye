package com.example.shohojbiye.domain.usecase

import javax.inject.Inject
import kotlin.math.ceil

class FeeCalculator @Inject constructor() {

    /**
     * Calculates the government registration fee based on the Mahr (Kabin) amount.
     * 
     * Formula:
     * - Up to 4,00,000 BDT: 1,250 BDT per 1,00,000 BDT (or fraction)
     * - Above 4,00,000 BDT: 100 BDT per additional 1,00,000 BDT (or fraction)
     * - Minimum fee: 200 BDT
     */
    fun calculateRegistrationFee(mahrAmount: Double): Double {
        if (mahrAmount <= 0) return 0.0

        val slab1Limit = 400000.0
        val slab1Rate = 1250.0
        val slab2Rate = 100.0
        val minimumFee = 200.0

        var totalFee = 0.0

        if (mahrAmount <= slab1Limit) {
            val units = ceil(mahrAmount / 100000.0)
            totalFee = units * slab1Rate
        } else {
            // First 4 lakhs
            totalFee = 4 * slab1Rate
            
            // Remaining amount
            val remaining = mahrAmount - slab1Limit
            val units = ceil(remaining / 100000.0)
            totalFee += units * slab2Rate
        }

        return maxOf(totalFee, minimumFee)
    }
}
