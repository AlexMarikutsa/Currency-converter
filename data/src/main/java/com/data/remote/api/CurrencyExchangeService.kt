package com.data.remote.api

import com.data.remote.network.NetworkResult
import com.domain.models.dto.CurrencyExchangeRatesDto
import retrofit2.http.GET

interface CurrencyExchangeService {

    @GET("currency-exchange-rates")
    suspend fun loadCurrencyExchangeRates(): NetworkResult<CurrencyExchangeRatesDto>
}