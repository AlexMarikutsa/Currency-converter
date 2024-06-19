package com.currencyexchanger.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyBalanceDvo
import com.currencyexchanger.presentation.screens.home.dvo.CurrencyExchangeDvo
import com.currencyexchanger.presentation.screens.home.event.Event
import com.currencyexchanger.presentation.screens.home.state.ScreenState
import com.currencyexchanger.presentation.screens.home.state.UiState
import com.domain.ResultState
import com.domain.exchange_strategies.FeeCalculationStrategy
import com.domain.exchange_strategies.FirstFiveCurrencyExchangesFreeStrategy
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

    private var successfulExchanges = 0

    private val formData = FormData()

    private val mutex = Mutex()
    private val currentCurrencyExchangeRates = mutableMapOf<String, Map<String, Double>>()

    private val userAvailableBalances = mutableMapOf<String, Double>()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Initial)
    val screenState = _screenState.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Default)
    val uiState = _uiState.asStateFlow()

    init {
        configUserAvailableBalances()
        loadData()
    }

    /**
     * function for configuring user's available balances. "USD" is added for example
     */
    private fun configUserAvailableBalances() {
        userAvailableBalances[DEFAULT_USER_CURRENCY] = 1000.0
        userAvailableBalances["USD"] = 0.0
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
            currentCurrencyExchangeRates[currencyRates.base] = currencyRates.rates
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
        return userAvailableBalances.map {
            CurrencyBalanceDvo(balance = it.value.toCurrency(), currency = it.key)
        }
    }

    private fun prepareCurrencyExchange(): CurrencyExchangeDvo {
        return CurrencyExchangeDvo(
            sell = CurrencyBalanceDvo(
                balance = formData.forSale.toCurrency(),
                currency = formData.currencyForSale
            ),
            receive = CurrencyBalanceDvo(
                balance = "${if (formData.receive > 0.0) "+" else ""}${formData.receive.toCurrency()}",
                currency = formData.currencyForReceive
            )
        )
    }

    private fun Double.toCurrency(): String {
        return String.format(Locale.US, "%.2f", this)
    }

    fun onEvent(event: Event) {
        if (_uiState.replayCache.firstOrNull() == UiState.Loading) return
        when (event) {
            Event.OnEnterAmountForSail -> _uiState.update { UiState.InputAmountForSale(amount = formData.forSale) }
            Event.OnChooseCurrencyForSail -> TODO()
            Event.OnChooseCurrencyForReceive -> TODO()
            Event.OnCloseDialog -> resetUiState()
            is Event.OnAmountForSailedEntered -> processAmountForSailedEntered(amount = event.amount)
        }
    }

    private fun processAmountForSailedEntered(amount: Double) {
        resetUiState()
        calculateCurrencyExchange(amount = amount, feeCalculationStrategy = FirstFiveCurrencyExchangesFreeStrategy(currencyExchangeNumber = successfulExchanges))
    }

    private fun calculateCurrencyExchange(
        amount: Double,
        feeCalculationStrategy: FeeCalculationStrategy
    ) {
        val amountForSale = checkAndPrepareAmountForSail(
            amount = amount
        )
        formData.forSale = amountForSale

        val exchangedAmountWithoutFee = prepareAmountForReceive(amount = amountForSale)

        val fee = feeCalculationStrategy.calculateFee(exchangedAmountWithoutFee)

        val exchangedAmountMinusFee = exchangedAmountWithoutFee - fee

        formData.receive = exchangedAmountMinusFee

        updateScreenState()
    }

    private fun checkAndPrepareAmountForSail(amount: Double): Double {
        val availableBalanceForSelectedCurrency = userAvailableBalances[formData.currencyForSale] ?: 0.0

        return when {
            availableBalanceForSelectedCurrency >= amount -> amount
            availableBalanceForSelectedCurrency > 0.0 -> availableBalanceForSelectedCurrency
            else -> 0.0
        }
    }

    private fun prepareAmountForReceive(amount: Double): Double {
        val rate = currentCurrencyExchangeRates[formData.currencyForSale]!![formData.currencyForReceive]!!
        return amount * rate
    }

    private fun resetUiState() {
        _uiState.update { UiState.Default }
    }

    companion object {
        const val DEFAULT_USER_CURRENCY = "EUR"
        const val DEFAULT_CURRENCY_FOR_RECEIVE = "USD"
        private const val REFRESH_TIME_IN_SECONDS = 5
    }
}