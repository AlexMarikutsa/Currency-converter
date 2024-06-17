package com.data.datasource

import com.data.remote.api.CurrencyExchangeService
import com.data.remote.network.toResultState
import com.domain.ResultState
import com.domain.datasource.CurrencyExchangeRemoteDataSource
import com.domain.models.dto.CurrencyExchangeRatesDto

class CurrencyExchangeRemoteDataSourceImpl(
    private val currencyExchangeService: CurrencyExchangeService
) : CurrencyExchangeRemoteDataSource {

    override suspend fun getRates(): ResultState<CurrencyExchangeRatesDto> {
        return currencyExchangeService.loadCurrencyExchangeRates().toResultState { it }
    }

}