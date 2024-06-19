package com.currencyexchanger.presentation.screens.home.state

sealed interface UiState {

    data object Default : UiState
    data object Loading : UiState
    data class InputAmountForSale(val amount: Double) : UiState
    data class SelectCurrencyForSale(
        val selectedIndex: Int,
        val currencies: Set<String>
    ) : UiState
    data class SelectCurrencyForReceive(
        val selectedIndex: Int,
        val currencies: Set<String>
    ) : UiState
    data class CurrencyConverted(val message: String) : UiState
}