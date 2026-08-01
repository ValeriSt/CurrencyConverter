package com.example.currencyconversionapp.domain.repository

import com.example.currencyconversionapp.domain.model.Currency
import com.example.currencyconversionapp.domain.model.ExchangeRate

interface CurrencyRepository {

    suspend fun getExchangeRate(
        sourceCurrency: Currency,
        targetCurrency: Currency,
        forceRefresh: Boolean = false
    ): Result<ExchangeRate>
}