package com.example.currencyconversionapp.domain.model

import java.math.BigDecimal

data class ExchangeRate(
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    val rate: BigDecimal,
    val lastUpdatedAtMillis: Long,
    val nextUpdateAtMillis: Long?
)
