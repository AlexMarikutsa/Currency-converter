package com.currencyexchanger.di

import com.data.datasource.CurrencyExchangeRemoteDataSourceImpl
import com.data.remote.api.CurrencyExchangeService
import com.data.repository.CurrencyExchangeRepositoryImpl
import com.domain.datasource.CurrencyExchangeRemoteDataSource
import com.domain.repository.CurrencyExchangeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideCurrencyExchangeService(retrofit: Retrofit): CurrencyExchangeService {
        return create(retrofit)
    }

    @Provides
    fun provideCurrencyExchangeRemoteDataSource(
        currencyExchangeService: CurrencyExchangeService
    ): CurrencyExchangeRemoteDataSource {
        return CurrencyExchangeRemoteDataSourceImpl(
            currencyExchangeService = currencyExchangeService
        )
    }

    @Provides
    fun provideCurrencyExchangeRepository(currencyExchangeRemoteDataSource: CurrencyExchangeRemoteDataSource): CurrencyExchangeRepository {
        return CurrencyExchangeRepositoryImpl(
            currencyExchangeRemoteDataSource = currencyExchangeRemoteDataSource
        )
    }

    private inline fun <reified T> create(retrofit: Retrofit): T {
        return retrofit.create(T::class.java)
    }
}