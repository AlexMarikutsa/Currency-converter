package com.domain.use_case

import com.domain.exchange_strategies.FeeCalculationStrategy
import com.domain.models.dto.ExchangeDto

class CalculateCurrencyExchangeUseCase {

    operator fun invoke(
        feeCalculationStrategy: FeeCalculationStrategy,
        rate: Double,
        forSale: Double
    ): ExchangeDto {
        val fee = feeCalculationStrategy.calculateFee(forSale)
        val received = prepareAmountForReceive(
            rate = rate, fee = fee, forSale = forSale
        )

        return ExchangeDto(fee = fee, result = received)
    }

    private fun prepareAmountForReceive(rate: Double, fee: Double, forSale: Double): Double {
        return (forSale - fee) * rate
    }
}