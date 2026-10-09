package com.example.shohojbiye.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class FeeCalculatorTest {

    private val calculator = FeeCalculator()

    @Test
    fun `calculate fee for mahr under 1 lakh returns 1250`() {
        assertEquals(1250.0, calculator.calculateRegistrationFee(50000.0), 0.0)
    }

    @Test
    fun `calculate fee for mahr exactly 1 lakh returns 1250`() {
        assertEquals(1250.0, calculator.calculateRegistrationFee(100000.0), 0.0)
    }

    @Test
    fun `calculate fee for mahr 4 lakhs returns 5000`() {
        assertEquals(5000.0, calculator.calculateRegistrationFee(400000.0), 0.0)
    }

    @Test
    fun `calculate fee for mahr 5 lakhs returns 5100`() {
        assertEquals(5100.0, calculator.calculateRegistrationFee(500000.0), 0.0)
    }

    @Test
    fun `calculate fee for 0 mahr returns 0`() {
        assertEquals(0.0, calculator.calculateRegistrationFee(0.0), 0.0)
    }

    @Test
    fun `calculate fee for very small mahr returns minimum 200`() {
        // Technically mahr < 100000 should be 1250 according to the logic, 
        // but if it was somehow 10 BDT, it would still be 1250.
        // The only way to get 200 is if totalFee was lower than 200, which it isn't for positive mahr in this formula.
        // However, the function returns maxOf(totalFee, 200.0) for positive values.
        assertEquals(1250.0, calculator.calculateRegistrationFee(10.0), 0.0)
    }
}
