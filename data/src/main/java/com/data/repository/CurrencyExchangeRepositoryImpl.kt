package com.data.repository

import com.domain.ResultState
import com.domain.datasource.CurrencyExchangeRemoteDataSource
import com.domain.models.dto.CurrencyExchangeRatesDto
import com.domain.repository.CurrencyExchangeRepository

class CurrencyExchangeRepositoryImpl(
    private val currencyExchangeRemoteDataSource: CurrencyExchangeRemoteDataSource
) : CurrencyExchangeRepository {

    override suspend fun getRates(): ResultState<CurrencyExchangeRatesDto> {
        return currencyExchangeRemoteDataSource.getRates()
    }

}