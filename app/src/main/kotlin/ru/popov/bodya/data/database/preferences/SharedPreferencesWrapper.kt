package ru.popov.bodya.data.database.preferences

import android.content.SharedPreferences
import ru.popov.bodya.data.database.currencies.entities.RatesEntity

/**
 * @author popovbodya
 */
class SharedPreferencesWrapper(private val sharedPreferences: SharedPreferences) {

    private companion object {
        const val EXCHANGE_RATE_USD_KEY = "usd_rate_key"
        const val EXCHANGE_RATE_EUR_KEY = "eur_rate_key"
        const val DEFAULT_USD_RATE = "0.015807"
        const val DEFAULT_EUR_RATE = "0.013662"
    }

    fun getRatesEntity(): RatesEntity {
        val usd = sharedPreferences.getString(EXCHANGE_RATE_USD_KEY, DEFAULT_USD_RATE)
        val eur = sharedPreferences.getString(EXCHANGE_RATE_EUR_KEY, DEFAULT_EUR_RATE)
        return RatesEntity(usd.toDouble(), eur.toDouble())
    }

    fun saveRatesEntity(ratesEntity: RatesEntity) {
        saveRate(EXCHANGE_RATE_USD_KEY, ratesEntity.usd.toString())
        saveRate(EXCHANGE_RATE_EUR_KEY, ratesEntity.eur.toString())
    }

    private fun saveRate(rateKey: String, rateValue: String) {
        val editor = sharedPreferences.edit()
        editor.putString(rateKey, rateValue)
        editor.apply()
    }
}