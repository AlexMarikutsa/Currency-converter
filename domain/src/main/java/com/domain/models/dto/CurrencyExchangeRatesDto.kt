package com.domain.models.dto

data class CurrencyExchangeRatesDto(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)