package com.domain.repository

import com.domain.ResultState
import com.domain.models.dto.CurrencyExchangeRatesDto

interface CurrencyExchangeRepository {

    suspend fun getRates(): ResultState<CurrencyExchangeRatesDto>

}