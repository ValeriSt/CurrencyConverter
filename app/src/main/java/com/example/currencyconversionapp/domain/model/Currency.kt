package com.example.currencyconversionapp.domain.model

enum class Currency(val displayName: String, val symbol: String) {
    EUR("Euro", "€"),
    USD("US Dollar", "$"),
    GBP("British Pound", "£"),
    BGN("Bulgarian Lev", "лв"),
    CHF("Swiss Franc", "CHF"),
    JPY("Japanese Yen", "¥"),
    CAD("Canadian Dollar", "C$"),
    AUD("Australian Dollar", "A$")
}
