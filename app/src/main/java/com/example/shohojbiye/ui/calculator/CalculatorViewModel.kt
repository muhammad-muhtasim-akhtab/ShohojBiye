package com.example.shohojbiye.ui.calculator

import androidx.lifecycle.ViewModel
import com.example.shohojbiye.domain.usecase.KabinCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val kabinCalculator: KabinCalculator
) : ViewModel() {

    private val _calculationResult = MutableStateFlow<KabinCalculator.KabinResult?>(null)
    val calculationResult = _calculationResult.asStateFlow()

    fun calculate(income: Double, groomEdu: Int, brideEdu: Int, areaType: String) {
        val result = kabinCalculator.calculate(
            income,
            groomEdu,
            brideEdu,
            areaType
        )
        _calculationResult.value = result
    }
}
