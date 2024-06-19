package com.currencyexchanger.presentation.screens.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchanger.R
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
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext
    context: Context,
    private val getCurrencyExchangeRatesUseCase: GetCurrencyExchangeRatesUseCase
) : AndroidViewModel(context as Application) {

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
            CurrencyBalanceDvo(
                balance = it.value.toCurrency(),
                currency = it.key
            )
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
            Event.OnEnterAmountForSale -> _uiState.update { UiState.InputAmountForSale(amount = formData.forSale) }
            Event.OnChooseCurrencyForSale -> processOnChooseCurrencyForSaleSelected()
            Event.OnChooseCurrencyForReceive -> processOnChooseCurrencyForReceiveSelected()
            Event.OnCloseDialog -> resetUiState()
            is Event.OnAmountForSaleEntered -> processAmountForSaleEntered(amount = event.amount)
            is Event.OnCurrencyForSaleSelected -> currencyForSaleChanged(currency = event.currency)
            is Event.OnCurrencyForReceiveSelected -> currencyForReceiveChanged(currency = event.currency)
            Event.OnSubmit -> submit()
        }
    }

    private fun submit() {
        if (formData.forSale > 0.0 && formData.receive > 0.0) {
            exchangeCurrency()
            currencySuccessfullyExchanged()
            ++successfulExchanges
            resetForm()
            updateScreenState()
        }
    }

    private fun currencySuccessfullyExchanged() {
        _uiState.update {
            UiState.CurrencyConverted(
                message = getApplication<Application>().getString(
                    R.string.currency_converted_dialog_message,
                    formData.forSale.toCurrency(),
                    formData.currencyForSale,
                    formData.receive.toCurrency(),
                    formData.currencyForReceive,
                    formData.fee.toCurrency(),
                    formData.currencyForSale
                )
            )
        }
    }

    private fun exchangeCurrency() {
        if (userAvailableBalances.containsKey(formData.currencyForReceive)) {
            userAvailableBalances[formData.currencyForReceive] =
                userAvailableBalances[formData.currencyForReceive]?.let { it + formData.receive } ?: formData.receive
        } else {
            userAvailableBalances[formData.currencyForReceive] = formData.receive
        }

        userAvailableBalances[formData.currencyForSale]?.let {
            userAvailableBalances[formData.currencyForSale] = if (it - formData.forSale >= 0) {
                it - formData.forSale
            } else {
                0.0
            }
        }
    }

    private fun resetForm() {
        formData.apply {
            forSale = 0.0
            receive = 0.0
            fee = 0.0
        }
    }

    private fun processOnChooseCurrencyForSaleSelected() {
        val currenciesForReceive = userAvailableBalances.keys
        _uiState.update {
            UiState.SelectCurrencyForSale(
                selectedIndex = currenciesForReceive.indexOf(formData.currencyForSale),
                currencies = currenciesForReceive
            )
        }
    }

    private fun currencyForReceiveChanged(currency: String) {
        resetUiState()
        formData.currencyForReceive = currency
        calculateCurrencyExchange(feeCalculationStrategy = prepareFeeStrategy())
        updateScreenState()
    }

    private fun processOnChooseCurrencyForReceiveSelected() {
        val currenciesForReceive = currentCurrencyExchangeRates[formData.currencyForSale]?.keys ?: return
        _uiState.update {
            UiState.SelectCurrencyForReceive(
                selectedIndex = currenciesForReceive.indexOf(formData.currencyForReceive),
                currencies = currenciesForReceive
            )
        }
    }

    private fun currencyForSaleChanged(currency: String) {
        resetUiState()
        formData.currencyForSale = currency
        calculateCurrencyExchange(feeCalculationStrategy = prepareFeeStrategy())
        updateScreenState()
    }

    private fun processAmountForSaleEntered(amount: Double) {
        resetUiState()
        calculateCurrencyExchange(amount = amount)
    }

    private fun calculateCurrencyExchange(
        amount: Double
    ) {
        val amountForSale = checkAndPrepareAmountForSale(
            amount = amount
        )
        formData.forSale = amountForSale

        calculateCurrencyExchange(feeCalculationStrategy = prepareFeeStrategy())
        updateScreenState()
    }

    private fun calculateCurrencyExchange(feeCalculationStrategy: FeeCalculationStrategy) {
        formData.fee = feeCalculationStrategy.calculateFee(formData.forSale)
        formData.receive = prepareAmountForReceive()
    }

    private fun prepareFeeStrategy(): FeeCalculationStrategy {
        return FirstFiveCurrencyExchangesFreeStrategy(currencyExchangeNumber = successfulExchanges)
    }

    private fun checkAndPrepareAmountForSale(amount: Double): Double {
        val availableBalanceForSelectedCurrency = userAvailableBalances[formData.currencyForSale] ?: 0.0

        return when {
            availableBalanceForSelectedCurrency >= amount -> amount
            availableBalanceForSelectedCurrency > 0.0 -> availableBalanceForSelectedCurrency
            else -> 0.0
        }
    }

    private fun prepareAmountForReceive(): Double {
        val rate = currentCurrencyExchangeRates[formData.currencyForSale]?.get(formData.currencyForReceive) ?: 0.0
        return (formData.forSale - formData.fee) * rate
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