package com.example.currencyconversionapp.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.currencyconversionapp.core.network.NetworkModule
import com.example.currencyconversionapp.domain.model.Currency
import com.example.currencyconversionapp.feature.conversion.CurrencyConversionView
import com.example.currencyconversionapp.feature.conversion.CurrencyConversionViewModel
import com.example.currencyconversionapp.feature.selection.CurrencySelectionView

private const val SELECTION_ROUTE = "selection"
private const val CONVERSION_ROUTE =
    "conversion/{amount}/{sourceCurrency}/{targetCurrency}"

@Composable
fun CurrencyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SELECTION_ROUTE
    ) {
        composable(SELECTION_ROUTE) {
            CurrencySelectionView(
                onContinue = { amount, source, target ->
                    navController.navigate(
                        "conversion/$amount/${source.name}/${target.name}"
                    )
                }
            )
        }
        composable(
            route = CONVERSION_ROUTE,
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("sourceCurrency") { type = NavType.StringType },
                navArgument("targetCurrency") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments
                ?.getString("amount")
                .orEmpty()
            val source = backStackEntry.arguments
                ?.getString("sourceCurrency")
                ?.let(Currency::valueOf)
                ?: Currency.EUR
            val target = backStackEntry.arguments
                ?.getString("targetCurrency")
                ?.let(Currency::valueOf)
                ?: Currency.USD
            val conversionViewModel: CurrencyConversionViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = CurrencyConversionViewModel.Factory(
                    initialAmount = amount,
                    initialSourceCurrency = source,
                    initialTargetCurrency = target,
                    repository = NetworkModule.repository
                )
            )

            CurrencyConversionView(
                viewModel = conversionViewModel,
                onBack = navController::popBackStack
            )
        }
    }
}
