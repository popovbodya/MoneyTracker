package ru.popov.bodya.data.network.api

import io.reactivex.Single
import ru.popov.bodya.BuildConfig
import ru.popov.bodya.data.network.beans.ExchangeRatesBean

/**
 * @author popovbodya
 */
class ExchangeRateApiWrapper(private val currenciesRateApi: CurrenciesRateApi) {

    companion object {
        const val API_KEY = "access_key"
        const val BASE_KEY = "base"
        const val BASE_VALUE = "RUB"
        const val SYMBOLS_KEY = "symbols"
        const val SYMBOLS_VALUE = "USD,EUR"
    }

    fun getCurrentRate(): Single<ExchangeRatesBean> = currenciesRateApi.getCurrentRate(buildQueryMap())

    private fun buildQueryMap(): Map<String, String> {
        return hashMapOf(
                API_KEY to BuildConfig.FIXER_API_KEY,
                BASE_KEY to BASE_VALUE,
                SYMBOLS_KEY to SYMBOLS_VALUE
        )
    }
}