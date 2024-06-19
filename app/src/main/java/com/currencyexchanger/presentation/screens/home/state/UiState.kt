package com.currencyexchanger.presentation.screens.home.state

sealed interface UiState {

    data object Default : UiState
    data object Loading : UiState
    data class InputAmountForSale(val amount: Double) : UiState
}