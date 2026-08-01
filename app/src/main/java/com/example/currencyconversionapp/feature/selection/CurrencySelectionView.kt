package com.example.currencyconversionapp.feature.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.currencyconversionapp.R
import com.example.currencyconversionapp.domain.model.Currency
import com.example.currencyconversionapp.shared.CurrencyDropdown
import com.example.currencyconversionapp.shared.CurrencyHero
import com.example.currencyconversionapp.shared.CurrencyScreenLayout

@Composable
fun CurrencySelectionView(
    onContinue: (String, Currency, Currency) -> Unit,
    viewModel: CurrencySelectionViewModel = viewModel()
) {
    val amount by viewModel.amount.collectAsStateWithLifecycle()
    val sourceCurrency by viewModel.sourceCurrency.collectAsStateWithLifecycle()
    val targetCurrency by viewModel.targetCurrency.collectAsStateWithLifecycle()
    val amountError by viewModel.amountError.collectAsStateWithLifecycle()

    val heroSpacing = dimensionResource(R.dimen.hero_spacing)
    val sectionSpacing = dimensionResource(R.dimen.section_spacing)
    val itemSpacing = dimensionResource(R.dimen.item_spacing)
    val smallSpacing = dimensionResource(R.dimen.small_spacing)
    val cardPadding = dimensionResource(R.dimen.card_padding)
    val cardRadius = dimensionResource(R.dimen.card_corner_radius)
    val componentRadius = dimensionResource(R.dimen.component_corner_radius)
    val buttonRadius = dimensionResource(R.dimen.button_corner_radius)
    val buttonHeight = dimensionResource(R.dimen.button_height)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val twoColumnMinWidth = dimensionResource(R.dimen.two_column_min_width)

    CurrencyScreenLayout {
        CurrencyHero(text = "FX")
        Spacer(Modifier.height(heroSpacing))
        Text(
            text = stringResource(R.string.selection_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(smallSpacing))
        Text(
            text = stringResource(R.string.selection_subtitle),
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
                Text(
                    text = stringResource(R.string.selection_card_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(smallSpacing))
                Text(
                    text = stringResource(R.string.selection_card_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                )
                Spacer(Modifier.height(itemSpacing))
                OutlinedTextField(
                    value = amount,
                    onValueChange = viewModel::onAmountChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.amount)) },
                    suffix = { Text(sourceCurrency.symbol) },
                    isError = amountError,
                    supportingText = {
                        if (amountError) {
                            Text(stringResource(R.string.amount_error))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    shape = RoundedCornerShape(componentRadius),
                    singleLine = true
                )
                Spacer(Modifier.height(itemSpacing))
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth < twoColumnMinWidth) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(itemSpacing)
                        ) {
                            CurrencyDropdown(
                                label = stringResource(R.string.from_currency),
                                selected = sourceCurrency,
                                onSelected = viewModel::onSourceCurrencySelected,
                                modifier = Modifier.fillMaxWidth()
                            )
                            CurrencyDropdown(
                                label = stringResource(R.string.to_currency),
                                selected = targetCurrency,
                                onSelected = viewModel::onTargetCurrencySelected,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CurrencyDropdown(
                                label = stringResource(R.string.from_currency),
                                selected = sourceCurrency,
                                onSelected = viewModel::onSourceCurrencySelected,
                                modifier = Modifier.weight(1f)
                            )
                            CurrencyDropdown(
                                label = stringResource(R.string.to_currency),
                                selected = targetCurrency,
                                onSelected = viewModel::onTargetCurrencySelected,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(sectionSpacing))
                Button(
                    onClick = {
                        if (viewModel.validate()) {
                            onContinue(amount, sourceCurrency, targetCurrency)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    shape = RoundedCornerShape(buttonRadius)
                ) {
                    Text(
                        text = stringResource(R.string.continue_action),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
