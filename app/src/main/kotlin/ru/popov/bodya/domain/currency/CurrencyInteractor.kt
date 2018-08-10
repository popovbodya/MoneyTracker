package ru.popov.bodya.domain.currency

import io.reactivex.Single
import ru.popov.bodya.data.repositories.CurrenciesRepository
import ru.popov.bodya.domain.currency.model.Rates

/**
 * @author popovbodya
 */
class CurrencyInteractor(private val currenciesRepository: CurrenciesRepository) {

    fun getExchangeRate(): Single<Rates> = currenciesRepository.getExchangeRateSingle()

    fun getCachedExchangeRate(): Single<Rates> = currenciesRepository.getCachedExchangeRateSingle()

    fun getUsdRate(usdRate: Double): Double = 1 / usdRate

    fun getEurRate(eurRate: Double): Double = 1 / eurRate
}