package com.example.currencyconversionapp.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.currencyconversionapp.R
import com.example.currencyconversionapp.domain.model.Currency

@Composable
fun CurrencyScreenLayout(
    content: @Composable ColumnScope.() -> Unit
) {
    val horizontalPadding = dimensionResource(R.dimen.screen_padding_horizontal)
    val verticalPadding = dimensionResource(R.dimen.screen_padding_vertical)
    val maxWidth = dimensionResource(R.dimen.content_max_width)
    val circleSize = dimensionResource(R.dimen.decorative_circle_size)
    val circleOffset = dimensionResource(R.dimen.decorative_circle_offset)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        DecorativeCircle(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = circleOffset, y = -circleOffset)
                .size(circleSize),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        )
        DecorativeCircle(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = -circleOffset, y = circleOffset)
                .size(circleSize),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .fillMaxWidth()
                .widthIn(max = maxWidth)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
private fun BoxScope.DecorativeCircle(
    modifier: Modifier,
    color: Color
) {
    Box(modifier = modifier.clip(CircleShape).background(color))
}

@Composable
fun CurrencyHero(text: String) {
    val heroSize = dimensionResource(R.dimen.hero_size)
    val radius = dimensionResource(R.dimen.hero_corner_radius)
    val elevation = dimensionResource(R.dimen.card_elevation)

    Surface(
        modifier = Modifier
            .size(heroSize)
            .shadow(elevation, RoundedCornerShape(radius)),
        shape = RoundedCornerShape(radius),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun CurrencyCodeChip(
    currency: Currency,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = "${currency.symbol}  ${currency.name}",
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.chip_padding_horizontal),
                vertical = dimensionResource(R.dimen.chip_padding_vertical)
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun Currency.localizedName(): String = stringResource(
    when (this) {
        Currency.EUR -> R.string.currency_eur
        Currency.USD -> R.string.currency_usd
        Currency.GBP -> R.string.currency_gbp
        Currency.BGN -> R.string.currency_bgn
        Currency.CHF -> R.string.currency_chf
        Currency.JPY -> R.string.currency_jpy
        Currency.CAD -> R.string.currency_cad
        Currency.AUD -> R.string.currency_aud
    }
)
