package ru.popov.bodya.data.repositories

import io.reactivex.Single
import ru.popov.bodya.data.database.currencies.converters.beans.RatesEntityBeanConverter
import ru.popov.bodya.data.database.currencies.converters.models.RatesEntityConverter
import ru.popov.bodya.data.database.currencies.entities.RatesEntity
import ru.popov.bodya.data.database.preferences.SharedPreferencesWrapper
import ru.popov.bodya.data.network.api.ExchangeRateApiWrapper
import ru.popov.bodya.domain.currency.model.Rates

/**
 * @author popovbodya
 */
class CurrenciesRepository(private val exchangeRateApiWrapper: ExchangeRateApiWrapper,
                           private val sharedPreferencesWrapper: SharedPreferencesWrapper,
                           private val ratesEntityBeanConverter: RatesEntityBeanConverter,
                           private val ratesEntityConverter: RatesEntityConverter) {

    fun getExchangeRateSingle(): Single<Rates> = exchangeRateApiWrapper
            .getCurrentRate()
            .map { ratesEntityBeanConverter.convert(it.rates) }
            .doOnSuccess { saveExchangeRate(it) }
            .map { ratesEntityConverter.convert(it) }

    fun getCachedExchangeRateSingle(): Single<Rates> =
            Single.fromCallable { sharedPreferencesWrapper.getRatesEntity() }
                    .map { ratesEntityConverter.convert(it) }

    fun getCachedExchangeRate(): Rates = ratesEntityConverter.convert(sharedPreferencesWrapper.getRatesEntity())

    private fun saveExchangeRate(ratesEntity: RatesEntity) {
        sharedPreferencesWrapper.saveRatesEntity(ratesEntity)
    }

}