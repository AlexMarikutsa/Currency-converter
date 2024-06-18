package com.currencyexchanger.presentation.screens.home.state

import com.currencyexchanger.presentation.screens.home.dvo.CurrencyBalanceDvo
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyExchangeDvo

sealed interface ScreenState {

    data object Initial : ScreenState
    data class Prepared(
        val myBalances: List<CurrencyBalanceDvo>,
        val currencyExchange: CurrencyExchangeDvo
    ) : ScreenState
}