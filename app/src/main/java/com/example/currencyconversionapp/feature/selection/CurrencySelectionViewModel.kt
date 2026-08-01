package com.example.currencyconversionapp.feature.selection

import androidx.lifecycle.ViewModel
import com.example.currencyconversionapp.domain.model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

class CurrencySelectionViewModel : ViewModel() {

    private val _amount = MutableStateFlow("")
    val amount = _amount.asStateFlow()

    private val _sourceCurrency = MutableStateFlow(Currency.EUR)
    val sourceCurrency = _sourceCurrency.asStateFlow()

    private val _targetCurrency = MutableStateFlow(Currency.USD)
    val targetCurrency = _targetCurrency.asStateFlow()

    private val _amountError = MutableStateFlow(false)
    val amountError = _amountError.asStateFlow()

    fun onAmountChanged(value: String) {
        _amount.value = normalizeAmount(value)
        _amountError.value = false
    }

    fun onSourceCurrencySelected(currency: Currency) {
        _sourceCurrency.value = currency
        if (currency == _targetCurrency.value) {
            _targetCurrency.value = Currency.entries.first { it != currency }
        }
    }

    fun onTargetCurrencySelected(currency: Currency) {
        _targetCurrency.value = currency
        if (currency == _sourceCurrency.value) {
            _sourceCurrency.value = Currency.entries.first { it != currency }
        }
    }

    fun validate(): Boolean {
        val valid = _amount.value.toBigDecimalOrNull()
            ?.let { it > BigDecimal.ZERO } == true
        _amountError.value = !valid
        return valid
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
}
