package com.domain.datasource

import com.domain.ResultState
import com.domain.models.dto.CurrencyExchangeRatesDto

interface CurrencyExchangeRemoteDataSource {

    suspend fun getRates(): ResultState<CurrencyExchangeRatesDto>

}