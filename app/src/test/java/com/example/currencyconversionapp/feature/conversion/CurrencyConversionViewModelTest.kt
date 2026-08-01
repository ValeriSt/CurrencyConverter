package com.example.currencyconversionapp.feature.conversion

import com.example.currencyconversionapp.domain.model.Currency
import com.example.currencyconversionapp.domain.model.ExchangeRate
import com.example.currencyconversionapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConversionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial rate load calculates the converted amount`() =
        runTest(testDispatcher) {
            val repository = FakeCurrencyRepository(
                result = successRate(rate = "1.25")
            )

            val viewModel = createViewModel(repository, amount = "100")
            advanceUntilIdle()

            assertEquals("125", viewModel.convertedAmount.value)
            assertEquals(BigDecimal("1.25"), viewModel.rate.value)
            assertFalse(viewModel.loading.value)
            assertNull(viewModel.error.value)
            assertEquals(1, repository.calls.size)
        }

    @Test
    fun `editing the amount recalculates without another API request`() =
        runTest(testDispatcher) {
            val repository = FakeCurrencyRepository(
                result = successRate(rate = "1.20")
            )
            val viewModel = createViewModel(repository, amount = "10")
            advanceUntilIdle()

            viewModel.onAmountChanged("25,50")

            assertEquals("25.50", viewModel.amount.value)
            assertEquals("30.6", viewModel.convertedAmount.value)
            assertEquals(1, repository.calls.size)
        }

    @Test
    fun `swap reverses currencies and uses the inverse rate locally`() =
        runTest(testDispatcher) {
            val repository = FakeCurrencyRepository(
                result = successRate(rate = "2.00")
            )
            val viewModel = createViewModel(repository, amount = "10")
            advanceUntilIdle()

            viewModel.swapCurrencies()

            assertEquals(Currency.USD, viewModel.sourceCurrency.value)
            assertEquals(Currency.EUR, viewModel.targetCurrency.value)
            assertEquals(BigDecimal("0.5000000000"), viewModel.rate.value)
            assertEquals("5", viewModel.convertedAmount.value)
            assertEquals(1, repository.calls.size)
        }

    @Test
    fun `retry forces a refreshed repository request`() =
        runTest(testDispatcher) {
            val repository = FakeCurrencyRepository(
                result = successRate(rate = "1.10")
            )
            val viewModel = createViewModel(repository)
            advanceUntilIdle()

            viewModel.retry()
            advanceUntilIdle()

            assertEquals(2, repository.calls.size)
            assertEquals(listOf(false, true), repository.calls.map { it.forceRefresh })
        }

    @Test
    fun `missing API key is mapped to a language neutral error`() =
        runTest(testDispatcher) {
            val repository = FakeCurrencyRepository(
                result = Result.failure(
                    IllegalArgumentException(
                        "Missing EXCHANGE_RATE_API_KEY in local.properties"
                    )
                )
            )

            val viewModel = createViewModel(repository)
            advanceUntilIdle()

            assertEquals(ConversionError.MISSING_API_KEY, viewModel.error.value)
            assertFalse(viewModel.loading.value)
            assertEquals("", viewModel.convertedAmount.value)
        }

    @Test
    fun `quota error is mapped without exposing the raw API message`() =
        runTest(testDispatcher) {
            val repository = FakeCurrencyRepository(
                result = Result.failure(IllegalStateException("quota-reached"))
            )

            val viewModel = createViewModel(repository)
            advanceUntilIdle()

            assertEquals(ConversionError.QUOTA_REACHED, viewModel.error.value)
        }

    private fun createViewModel(
        repository: CurrencyRepository,
        amount: String = "100"
    ): CurrencyConversionViewModel = CurrencyConversionViewModel(
        initialAmount = amount,
        initialSourceCurrency = Currency.EUR,
        initialTargetCurrency = Currency.USD,
        repository = repository
    )

    private fun successRate(rate: String): Result<ExchangeRate> = Result.success(
        ExchangeRate(
            baseCurrency = Currency.EUR,
            targetCurrency = Currency.USD,
            rate = BigDecimal(rate),
            lastUpdatedAtMillis = 1_000L,
            nextUpdateAtMillis = 2_000L
        )
    )

    private class FakeCurrencyRepository(
        var result: Result<ExchangeRate>
    ) : CurrencyRepository {

        data class Call(
            val sourceCurrency: Currency,
            val targetCurrency: Currency,
            val forceRefresh: Boolean
        )

        val calls = mutableListOf<Call>()

        override suspend fun getExchangeRate(
            sourceCurrency: Currency,
            targetCurrency: Currency,
            forceRefresh: Boolean
        ): Result<ExchangeRate> {
            calls += Call(sourceCurrency, targetCurrency, forceRefresh)
            return result
        }
    }
}