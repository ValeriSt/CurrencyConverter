package com.example.currencyconversionapp.feature.conversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.currencyconversionapp.domain.model.Currency
import com.example.currencyconversionapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyConversionViewModel(
    initialAmount: String,
    initialSourceCurrency: Currency,
    initialTargetCurrency: Currency,
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _amount = MutableStateFlow(initialAmount)
    val amount = _amount.asStateFlow()

    private val _sourceCurrency = MutableStateFlow(initialSourceCurrency)
    val sourceCurrency = _sourceCurrency.asStateFlow()

    private val _targetCurrency = MutableStateFlow(initialTargetCurrency)
    val targetCurrency = _targetCurrency.asStateFlow()

    private val _convertedAmount = MutableStateFlow("")
    val convertedAmount = _convertedAmount.asStateFlow()

    private val _rate = MutableStateFlow<BigDecimal?>(null)
    val rate = _rate.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<ConversionError?>(null)
    val error = _error.asStateFlow()

    init {
        loadRate()
    }

    fun onAmountChanged(value: String) {
        _amount.value = normalizeAmount(value)
        calculate()
    }

    fun swapCurrencies() {
        val oldSource = _sourceCurrency.value
        _sourceCurrency.value = _targetCurrency.value
        _targetCurrency.value = oldSource
        _rate.value = _rate.value
            ?.takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
            ?.let {
                BigDecimal.ONE.divide(it, CALCULATION_SCALE, RoundingMode.HALF_UP)
            }
        calculate()
    }

    fun retry() {
        loadRate(forceRefresh = true)
    }

    private fun loadRate(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.getExchangeRate(
                sourceCurrency = _sourceCurrency.value,
                targetCurrency = _targetCurrency.value,
                forceRefresh = forceRefresh
            ).onSuccess { exchangeRate ->
                _rate.value = exchangeRate.rate
                calculate()
            }.onFailure { throwable ->
                _error.value = when {
                    throwable.message?.contains("API_KEY") == true ->
                        ConversionError.MISSING_API_KEY
                    throwable.message?.contains("invalid-key") == true ||
                        throwable.message?.contains("inactive-account") == true ->
                        ConversionError.INVALID_API_KEY
                    throwable.message?.contains("quota-reached") == true ->
                        ConversionError.QUOTA_REACHED
                    else -> ConversionError.GENERIC
                }
            }
            _loading.value = false
        }
    }

    private fun calculate() {
        val input = _amount.value.toBigDecimalOrNull()
        val currentRate = _rate.value
        _convertedAmount.value =
            if (input == null || currentRate == null) {
                ""
            } else {
                input.multiply(currentRate)
                    .setScale(DISPLAY_SCALE, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            }
    }

    private fun normalizeAmount(value: String): String {
        val normalized = value.replace(',', '.')
        var decimalSeparatorSeen = false
        return buildString {
            normalized.forEach { character ->
                when {
                    character.isDigit() -> append(character)
                    character == '.' && !decimalSeparatorSeen -> {
                        append(character)
                        decimalSeparatorSeen = true
                    }
                }
            }
        }
    }

    class Factory(
        private val initialAmount: String,
        private val initialSourceCurrency: Currency,
        private val initialTargetCurrency: Currency,
        private val repository: CurrencyRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CurrencyConversionViewModel::class.java))
            return CurrencyConversionViewModel(
                initialAmount,
                initialSourceCurrency,
                initialTargetCurrency,
                repository
            ) as T
        }
    }

    private companion object {
        const val CALCULATION_SCALE = 10
        const val DISPLAY_SCALE = 2
    }
}

enum class ConversionError {
    MISSING_API_KEY,
    INVALID_API_KEY,
    QUOTA_REACHED,
    GENERIC
}
