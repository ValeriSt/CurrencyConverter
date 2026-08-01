package com.example.currencyconversionapp.core.network.dto

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponseDto(
    val result: String,
    @SerializedName("base_code")
    val baseCode: String?,
    @SerializedName("time_last_update_unix")
    val lastUpdateUnix: Long?,
    @SerializedName("time_next_update_unix")
    val nextUpdateUnix: Long?,
    @SerializedName("conversion_rates")
    val conversionRates: Map<String, Double>?,
    @SerializedName("error-type")
    val errorType: String? = null
)
