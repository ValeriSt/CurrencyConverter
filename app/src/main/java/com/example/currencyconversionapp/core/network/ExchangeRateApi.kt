package com.example.currencyconversionapp.core.network

import com.example.currencyconversionapp.core.network.dto.ExchangeRatesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {

    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRatesResponseDto
}
