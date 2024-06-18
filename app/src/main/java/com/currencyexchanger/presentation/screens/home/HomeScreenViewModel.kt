package com.currencyexchanger.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyBalanceDvo
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyExchangeDvo
import com.currencyexchanger.presentation.screens.home.state.ScreenState
import com.domain.ResultState
import com.domain.models.dto.CurrencyExchangeRatesDto
import com.domain.use_case.GetCurrencyExchangeRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCurrencyExchangeRatesUseCase: GetCurrencyExchangeRatesUseCase
) : ViewModel() {

    private val mutex = Mutex()
    private var currentCurrencyExchangeRates: CurrencyExchangeRatesDto? = null

    private val currentBalances = mutableMapOf(DEFAULT_USER_CURRENCY to 1000.0)

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Initial)
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            loadCurrencyExchangeRates()
        }
    }

    private suspend fun loadCurrencyExchangeRates() {
        when (val currencyRates = getCurrencyExchangeRatesUseCase()) {
            is ResultState.Error -> loadCurrencyExchangeRates()
            is ResultState.Success -> currencyRates.data?.let { processCurrencyRates(currencyRates = it) } ?: loadCurrencyExchangeRates()
        }
    }

    private suspend fun processCurrencyRates(currencyRates: CurrencyExchangeRatesDto) {
        mutex.withLock {
            currentCurrencyExchangeRates = currencyRates
            updateScreenState()
        }
        delay(REFRESH_TIME_IN_SECONDS * 1000L)
        loadCurrencyExchangeRates()
    }

    private fun updateScreenState() {
        _screenState.update {
            ScreenState.Prepared(
                myBalances = prepareMyBalances(),
                currencyExchange = prepareCurrencyExchange()
            )
        }
    }

    private fun prepareMyBalances(): List<CurrencyBalanceDvo> {
        return currentBalances.map {
            CurrencyBalanceDvo(balance = it.value.toCurrency(), currency = it.key)
        }
    }

    private fun prepareCurrencyExchange(): CurrencyExchangeDvo {
        return CurrencyExchangeDvo(
            sell = CurrencyBalanceDvo(
                balance = 100.0.toCurrency(),
                currency = DEFAULT_USER_CURRENCY
            ),
            receive = CurrencyBalanceDvo(
                balance = "+${110.3.toCurrency()}",
                currency = "USD"
            )
        )
    }

    private fun Double.toCurrency(): String {
        return String.format(Locale.US, "%.2f", this)
    }

    companion object {
        private const val DEFAULT_USER_CURRENCY = "EUR"
        private const val REFRESH_TIME_IN_SECONDS = 5
    }
}