package com.example.currencyconversionapp.core.network

import com.example.currencyconversionapp.BuildConfig
import com.example.currencyconversionapp.data.repository.CurrencyRepositoryImpl
import com.example.currencyconversionapp.domain.repository.CurrencyRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL =
        "https://v6.exchangerate-api.com/"

    private val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }

    val repository: CurrencyRepository by lazy {
        CurrencyRepositoryImpl(
            api = api,
            apiKey = BuildConfig.EXCHANGE_RATE_API_KEY
        )
    }
}
