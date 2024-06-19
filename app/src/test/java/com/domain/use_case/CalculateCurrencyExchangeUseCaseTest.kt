package com.domain.use_case

import com.domain.exchange_strategies.FirstFiveCurrencyExchangesFreeStrategy
import org.junit.Assert.*
import org.junit.Test


class CalculateCurrencyExchangeUseCaseTest {

    @Test
    fun `Strategy FirstFiveCurrencyExchangesFreeStrategy calculate fee`() {
        val useCase = CalculateCurrencyExchangeUseCase()
        val result = useCase.invoke(
            feeCalculationStrategy = FirstFiveCurrencyExchangesFreeStrategy(
                currencyExchangeNumber = 2
            ),
            rate = 2.0,
            forSale = 100.0
        )

        assertEquals(0.toFloat(), result.fee.toFloat())

        val useCase2 = CalculateCurrencyExchangeUseCase()
        val result2 = useCase2.invoke(
            feeCalculationStrategy = FirstFiveCurrencyExchangesFreeStrategy(
                currencyExchangeNumber = 8
            ),
            rate = 2.0,
            forSale = 100.0
        )

        assertEquals(0.7.toFloat(), result2.fee.toFloat())
    }

    @Test
    fun `Strategy FirstFiveCurrencyExchangesFreeStrategy without fee`() {
        val useCase = CalculateCurrencyExchangeUseCase()
        val result = useCase.invoke(
            feeCalculationStrategy = FirstFiveCurrencyExchangesFreeStrategy(
                currencyExchangeNumber = 2
            ),
            rate = 2.0,
            forSale = 100.0
        )

        assertEquals(200.0.toFloat(), result.result.toFloat())
    }

    @Test
    fun `Strategy FirstFiveCurrencyExchangesFreeStrategy with fee`() {
        val useCase = CalculateCurrencyExchangeUseCase()
        val result = useCase.invoke(
            feeCalculationStrategy = FirstFiveCurrencyExchangesFreeStrategy(
                currencyExchangeNumber = 6
            ),
            rate = 2.0,
            forSale = 100.0
        )

        assertEquals(198.6.toFloat(), result.result.toFloat())
    }
}