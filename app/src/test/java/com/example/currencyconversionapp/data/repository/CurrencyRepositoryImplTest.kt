package com.example.currencyconversionapp.data.repository

import com.example.currencyconversionapp.core.network.ExchangeRateApi
import com.example.currencyconversionapp.core.network.dto.ExchangeRatesResponseDto
import com.example.currencyconversionapp.domain.model.Currency
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyRepositoryImplTest {

    @Test
    fun `fresh rates are cached and reused for the same base currency`() = runTest {
        var apiCalls = 0
        val api = object : ExchangeRateApi {
            override suspend fun getLatestRates(
                apiKey: String,
                baseCurrency: String
            ): ExchangeRatesResponseDto {
                apiCalls++
                return ExchangeRatesResponseDto(
                    result = "success",
                    baseCode = "EUR",
                    lastUpdateUnix = 1_000,
                    nextUpdateUnix = 5_000,
                    conversionRates = mapOf("USD" to 1.2, "GBP" to 0.8)
                )
            }
        }
        val repository = CurrencyRepositoryImpl(
            api = api,
            apiKey = "test-key",
            currentTimeMillis = { 2_000_000 }
        )

        repository.getExchangeRate(Currency.EUR, Currency.USD).getOrThrow()
        repository.getExchangeRate(Currency.EUR, Currency.GBP).getOrThrow()

        assertEquals(1, apiCalls)
    }
}
