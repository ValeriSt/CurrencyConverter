package com.example.currencyconversionapp.feature.conversion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.currencyconversionapp.R
import com.example.currencyconversionapp.shared.CurrencyCodeChip
import com.example.currencyconversionapp.shared.CurrencyHero
import com.example.currencyconversionapp.shared.CurrencyScreenLayout

@Composable
fun CurrencyConversionView(
    viewModel: CurrencyConversionViewModel,
    onBack: () -> Unit
) {
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val sourceCurrency by viewModel.sourceCurrency.collectAsStateWithLifecycle()
    val targetCurrency by viewModel.targetCurrency.collectAsStateWithLifecycle()
    val convertedAmount by viewModel.convertedAmount.collectAsStateWithLifecycle()
    val rate by viewModel.rate.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val errorText = error?.let { stringResource(it.stringResource()) }

    val heroSpacing = dimensionResource(R.dimen.hero_spacing)
    val sectionSpacing = dimensionResource(R.dimen.section_spacing)
    val itemSpacing = dimensionResource(R.dimen.item_spacing)
    val smallSpacing = dimensionResource(R.dimen.small_spacing)
    val tinySpacing = dimensionResource(R.dimen.tiny_spacing)
    val cardPadding = dimensionResource(R.dimen.card_padding)
    val cardRadius = dimensionResource(R.dimen.card_corner_radius)
    val componentRadius = dimensionResource(R.dimen.component_corner_radius)
    val buttonRadius = dimensionResource(R.dimen.button_corner_radius)
    val buttonHeight = dimensionResource(R.dimen.button_height)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val resultMinHeight = dimensionResource(R.dimen.result_min_height)
    val progressSize = dimensionResource(R.dimen.progress_size)

    CurrencyScreenLayout {
        CurrencyHero(text = "FX")
        Spacer(Modifier.height(heroSpacing))
        Text(
            text = stringResource(R.string.conversion_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(smallSpacing))
        Text(
            text = stringResource(
                R.string.conversion_subtitle,
                sourceCurrency.name,
                targetCurrency.name
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
        )
        Spacer(Modifier.height(sectionSpacing))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cardRadius),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(cardElevation)
        ) {
            Column(modifier = Modifier.padding(cardPadding)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = viewModel::onAmountChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            stringResource(
                                R.string.amount_in,
                                sourceCurrency.name
                            )
                        )
                    },
                    suffix = { Text(sourceCurrency.symbol) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    shape = RoundedCornerShape(componentRadius),
                    singleLine = true
                )
                Spacer(Modifier.height(itemSpacing))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(smallSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CurrencyCodeChip(sourceCurrency)
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        CurrencyCodeChip(targetCurrency)
                    }
                    FilledTonalButton(
                        onClick = viewModel::swapCurrencies,
                        enabled = rate != null,
                        shape = RoundedCornerShape(buttonRadius)
                    ) {
                        Text(stringResource(R.string.swap_currencies))
                    }
                }
            }
        }

        Spacer(Modifier.height(itemSpacing))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = resultMinHeight),
            shape = RoundedCornerShape(cardRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = resultMinHeight)
                    .padding(cardPadding),
                contentAlignment = Alignment.Center
            ) {
                when {
                    loading -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(smallSpacing)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(progressSize),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(stringResource(R.string.loading_rate))
                    }

                    error != null -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(smallSpacing)
                    ) {
                        Text(
                            text = errorText.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = viewModel::retry) {
                            Text(stringResource(R.string.retry_action))
                        }
                    }

                    else -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.converted_amount_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
                        )
                        Spacer(Modifier.height(tinySpacing))
                        Text(
                            text = if (convertedAmount.isBlank()) {
                                stringResource(R.string.empty_result)
                            } else {
                                stringResource(
                                    R.string.amount_with_symbol,
                                    convertedAmount,
                                    targetCurrency.symbol
                                )
                            },
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        rate?.let {
                            Spacer(Modifier.height(smallSpacing))
                            Text(
                                text = stringResource(
                                    R.string.rate_pattern,
                                    sourceCurrency.name,
                                    it.stripTrailingZeros().toPlainString(),
                                    targetCurrency.name
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(sectionSpacing))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            shape = RoundedCornerShape(buttonRadius)
        ) {
            Text(
                text = stringResource(R.string.back_action),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun ConversionError.stringResource(): Int = when (this) {
    ConversionError.MISSING_API_KEY -> R.string.error_missing_api_key
    ConversionError.INVALID_API_KEY -> R.string.error_invalid_api_key
    ConversionError.QUOTA_REACHED -> R.string.error_quota_reached
    ConversionError.GENERIC -> R.string.error_loading_rate
}
