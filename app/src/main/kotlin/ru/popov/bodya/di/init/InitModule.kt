package ru.popov.bodya.di.init

import dagger.Module
import dagger.Provides
import ru.popov.bodya.data.database.currencies.converters.beans.RatesEntityBeanConverter
import ru.popov.bodya.data.database.currencies.converters.models.RatesEntityConverter
import ru.popov.bodya.data.database.preferences.SharedPreferencesWrapper
import ru.popov.bodya.data.network.api.ExchangeRateApiWrapper
import ru.popov.bodya.data.repositories.CurrenciesRepository
import ru.popov.bodya.domain.currency.CurrencyInteractor

/**
 *  @author popovbodya
 */
@Module
class InitModule {
    @Provides
    fun provideCurrenciesInteractor(currenciesRepository: CurrenciesRepository): CurrencyInteractor =
            CurrencyInteractor(currenciesRepository)

    @Provides
    fun provideCurrenciesRepository(exchangeRateApiWrapper: ExchangeRateApiWrapper, sharedPreferencesWrapper: SharedPreferencesWrapper): CurrenciesRepository =
            CurrenciesRepository(exchangeRateApiWrapper, sharedPreferencesWrapper, RatesEntityBeanConverter(), RatesEntityConverter())
}