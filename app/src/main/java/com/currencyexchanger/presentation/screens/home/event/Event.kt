package com.currencyexchanger.presentation.screens.home.event

sealed interface Event {

    data object OnEnterAmountForSale : Event
    data object OnChooseCurrencyForSale : Event
    data object OnChooseCurrencyForReceive : Event
    data object OnCloseDialog : Event
    data class OnAmountForSaleEntered(
        val amount: Double
    ) : Event
    data class OnCurrencyForSaleSelected(val currency: String) : Event
    data class OnCurrencyForReceiveSelected(val currency: String) : Event
}