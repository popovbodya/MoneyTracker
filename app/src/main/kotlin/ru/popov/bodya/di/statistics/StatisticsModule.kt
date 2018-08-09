package ru.popov.bodya.di.statistics

import dagger.Module
import dagger.Provides
import ru.popov.bodya.data.repositories.CurrenciesRepository
import ru.popov.bodya.domain.statistics.StatisticsInteractor

/**
 *  @author popovbodya
 */
@Module
class StatisticsModule {

    @Provides
    fun provideStatisticInteractor(currenciesRepository: CurrenciesRepository) = StatisticsInteractor(currenciesRepository)
}