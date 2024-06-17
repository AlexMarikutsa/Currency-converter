package com.data.datasource

import com.domain.ResultState
import com.domain.datasource.CurrencyExchangeRemoteDataSource
import com.domain.models.dto.CurrencyExchangeRatesDto

class CurrencyExchangeRemoteDataSourceImpl : CurrencyExchangeRemoteDataSource {

    override suspend fun getRates(): ResultState<CurrencyExchangeRatesDto> {
        TODO("Not yet implemented")
    }

}