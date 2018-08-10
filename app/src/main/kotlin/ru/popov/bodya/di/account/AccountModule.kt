package ru.popov.bodya.di.account

import dagger.Module
import dagger.Provides
import ru.popov.bodya.data.database.currencies.converters.beans.RatesEntityBeanConverter
import ru.popov.bodya.data.database.currencies.converters.models.RatesEntityConverter
import ru.popov.bodya.data.database.preferences.SharedPreferencesWrapper
import ru.popov.bodya.data.database.transactions.converters.models.PeriodicalTransactionEntityConverter
import ru.popov.bodya.data.database.transactions.converters.models.TransactionEntityConverter
import ru.popov.bodya.data.database.transactions.dao.PeriodicalTransactionsDao
import ru.popov.bodya.data.database.transactions.dao.TransactionsDao
import ru.popov.bodya.data.network.api.ExchangeRateApiWrapper
import ru.popov.bodya.data.repositories.CurrenciesRepository
import ru.popov.bodya.data.repositories.PeriodicalTransactionsRepository
import ru.popov.bodya.data.repositories.TransactionsRepository
import ru.popov.bodya.domain.calendar.CalendarInteractor
import ru.popov.bodya.domain.currency.CurrencyInteractor
import ru.popov.bodya.domain.transactions.PeriodicalTransactionsInteractor
import ru.popov.bodya.domain.transactions.TransactionsInteractor

/**
 * @author popovbodya
 */
@Module
class AccountModule {

    @Provides
    fun provideCurrenciesInteractor(currenciesRepository: CurrenciesRepository): CurrencyInteractor =
            CurrencyInteractor(currenciesRepository)

    @Provides
    fun provideCalendarInteractor(transactionsRepository: TransactionsRepository) = CalendarInteractor(transactionsRepository)

    @Provides
    fun provideTransactionsInteractor(transactionsRepository: TransactionsRepository): TransactionsInteractor =
            TransactionsInteractor(transactionsRepository)

    @Provides
    fun providePeriodicalTransactionsInteractor(periodicalTransactionsRepository: PeriodicalTransactionsRepository) =
            PeriodicalTransactionsInteractor(periodicalTransactionsRepository)

    @Provides
    fun provideCurrenciesRepository(exchangeRateApiWrapper: ExchangeRateApiWrapper, sharedPreferencesWrapper: SharedPreferencesWrapper): CurrenciesRepository =
            CurrenciesRepository(exchangeRateApiWrapper, sharedPreferencesWrapper, RatesEntityBeanConverter(), RatesEntityConverter())

    @Provides
    fun provideTransactionsRepository(transactionsDao: TransactionsDao): TransactionsRepository =
            TransactionsRepository(transactionsDao, TransactionEntityConverter())

    @Provides
    fun providePeriodicalTransactionsRepository(periodicalTransactionsDao: PeriodicalTransactionsDao) =
            PeriodicalTransactionsRepository(periodicalTransactionsDao, PeriodicalTransactionEntityConverter())

}