package com.currencyexchanger.presentation.screens.home.dvo

data class CurrencyExchangeDvo(
    val sell: CurrencyBalanceDvo,
    val receive: CurrencyBalanceDvo
)
