package com.example.currencyconversionapp.data.repository

import com.example.currencyconversionapp.core.network.ExchangeRateApi
import com.example.currencyconversionapp.domain.model.Currency
import com.example.currencyconversionapp.domain.model.ExchangeRate
import com.example.currencyconversionapp.domain.repository.CurrencyRepository
import java.math.BigDecimal

class CurrencyRepositoryImpl(
    private val api: ExchangeRateApi,
    private val apiKey: String,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis
) : CurrencyRepository {

    private data class RateSnapshot(
        val rates: Map<String, Double>,
        val lastUpdatedAtMillis: Long,
        val expiresAtMillis: Long
    )

    private val cachedRates = mutableMapOf<Currency, RateSnapshot>()

    override suspend fun getExchangeRate(
        sourceCurrency: Currency,
        targetCurrency: Currency,
        forceRefresh: Boolean
    ): Result<ExchangeRate> {
        return runCatching {
            require(apiKey.isNotBlank()) {
                "Missing EXCHANGE_RATE_API_KEY in local.properties"
            }

            val now = currentTimeMillis()
            val cachedSnapshot = cachedRates[sourceCurrency]
            val snapshot = if (
                !forceRefresh &&
                cachedSnapshot != null &&
                now < cachedSnapshot.expiresAtMillis
            ) {
                cachedSnapshot
            } else {
                val response = api.getLatestRates(
                    apiKey = apiKey,
                    baseCurrency = sourceCurrency.name
                )

                check(response.result == "success") {
                    response.errorType ?: "Unknown API error"
                }

                val rates = requireNotNull(response.conversionRates) {
                    "The API returned no conversion rates"
                }

                RateSnapshot(
                    rates = rates,
                    lastUpdatedAtMillis =
                        (response.lastUpdateUnix ?: 0L) * 1_000L,
                    expiresAtMillis =
                        response.nextUpdateUnix?.times(1_000L)
                            ?: now + CACHE_FALLBACK_MILLIS
                ).also { cachedRates[sourceCurrency] = it }
            }

            val targetRate = snapshot.rates[targetCurrency.name]
                ?: error("Unsupported currency: ${targetCurrency.name}")

            ExchangeRate(
                baseCurrency = sourceCurrency,
                targetCurrency = targetCurrency,
                rate = BigDecimal.valueOf(targetRate),
                lastUpdatedAtMillis = snapshot.lastUpdatedAtMillis,
                nextUpdateAtMillis = snapshot.expiresAtMillis
            )
        }
    }

    private companion object {
        const val CACHE_FALLBACK_MILLIS = 12 * 60 * 60 * 1_000L
    }
}
