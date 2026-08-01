package com.example.currencyconversionapp.feature.selection

import com.example.currencyconversionapp.domain.model.Currency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencySelectionViewModelTest {

    @Test
    fun `amount input accepts one decimal separator`() {
        val viewModel = CurrencySelectionViewModel()

        viewModel.onAmountChanged("12,3.4 EUR")

        assertEquals("12.34", viewModel.amount.value)
    }

    @Test
    fun `validate rejects zero and accepts a positive amount`() {
        val viewModel = CurrencySelectionViewModel()

        viewModel.onAmountChanged("0")
        assertFalse(viewModel.validate())

        viewModel.onAmountChanged("10.50")
        assertTrue(viewModel.validate())
    }

    @Test
    fun `selecting the same currency keeps the pair different`() {
        val viewModel = CurrencySelectionViewModel()

        viewModel.onTargetCurrencySelected(Currency.EUR)

        assertEquals(Currency.EUR, viewModel.targetCurrency.value)
        assertTrue(viewModel.sourceCurrency.value != viewModel.targetCurrency.value)
    }
}
