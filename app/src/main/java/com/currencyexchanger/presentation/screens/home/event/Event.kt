package com.currencyexchanger.presentation.screens.home.event

sealed interface Event {

    data object OnEnterAmountForSail : Event
    data object OnChooseCurrencyForSail : Event
    data object OnChooseCurrencyForReceive : Event
    data object OnCloseDialog : Event
    data class OnAmountForSailedEntered(
        val amount: Double
    ) : Event
}