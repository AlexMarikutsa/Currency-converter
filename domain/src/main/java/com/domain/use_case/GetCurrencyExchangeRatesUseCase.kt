package com.domain.use_case

import com.domain.ResultState
import com.domain.models.dto.CurrencyExchangeRatesDto
import com.domain.repository.CurrencyExchangeRepository

class GetCurrencyExchangeRatesUseCase(
    private val currencyExchangeRepository: CurrencyExchangeRepository
) {

    suspend operator fun invoke(): ResultState<CurrencyExchangeRatesDto> {
        return currencyExchangeRepository.getRates()
    }
}