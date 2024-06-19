package com.currencyexchanger.presentation.screens.home

import com.currencyexchanger.presentation.screens.home.HomeScreenViewModel.Companion.DEFAULT_CURRENCY_FOR_RECEIVE
import com.currencyexchanger.presentation.screens.home.HomeScreenViewModel.Companion.DEFAULT_USER_CURRENCY

data class FormData(
    var forSale: Double = 0.0,
    var receive: Double = 0.0,
    var fee: Double = 0.0,
    var currencyForSale: String = DEFAULT_USER_CURRENCY,
    var currencyForReceive: String = DEFAULT_CURRENCY_FOR_RECEIVE
)
