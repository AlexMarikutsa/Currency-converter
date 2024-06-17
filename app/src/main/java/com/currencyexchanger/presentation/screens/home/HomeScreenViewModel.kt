package com.currencyexchanger.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.use_case.GetCurrencyExchangeRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCurrencyExchangeRatesUseCase: GetCurrencyExchangeRatesUseCase
) : ViewModel() {

    init {
        testLoadCurrency()
    }

    private fun testLoadCurrency() {
        viewModelScope.launch {
            getCurrencyExchangeRatesUseCase()
        }
    }
}