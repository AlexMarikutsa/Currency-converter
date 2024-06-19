package com.currencyexchanger.di

import com.domain.repository.CurrencyExchangeRepository
import com.domain.use_case.CalculateCurrencyExchangeUseCase
import com.domain.use_case.GetCurrencyExchangeRatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    fun provideGetCurrencyExchangeRatesUseCase(currencyExchangeRepository: CurrencyExchangeRepository): GetCurrencyExchangeRatesUseCase {
        return GetCurrencyExchangeRatesUseCase(
            currencyExchangeRepository = currencyExchangeRepository
        )
    }

    @Provides
    fun provideCalculateCurrencyExchangeUseCase(): CalculateCurrencyExchangeUseCase {
        return CalculateCurrencyExchangeUseCase()
    }
}