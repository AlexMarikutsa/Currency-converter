package com.domain.exchange_strategies

class FirstFiveCurrencyExchangesFreeStrategy(
    private val currencyExchangeNumber: Int
) : FeeCalculationStrategy {

    override fun calculateFee(amount: Double): Double {
        return if (currencyExchangeNumber >= FREE_EXCHANGES_COUNT) {
            amount * FEE_PERCENTAGE
        } else {
            0.0
        }
    }

    private companion object {
        private const val FREE_EXCHANGES_COUNT = 5
        private const val FEE_PERCENTAGE = 0.7
    }
}