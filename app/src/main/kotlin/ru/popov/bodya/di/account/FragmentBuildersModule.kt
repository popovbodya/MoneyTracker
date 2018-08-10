package ru.popov.bodya.di.account

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.popov.bodya.di.statistics.StatisticsModule
import ru.popov.bodya.presentation.about.AboutFragment
import ru.popov.bodya.presentation.account.AccountFragment
import ru.popov.bodya.presentation.addtransaction.AddTransactionFragment
import ru.popov.bodya.presentation.periodical.PeriodicalTransactionsFragment
import ru.popov.bodya.presentation.statistics.StatisticsFragment

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeWalletFragment(): AccountFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeAddTransactionFragment(): AddTransactionFragment

    @ContributesAndroidInjector(modules = [StatisticsModule::class])
    abstract fun contributeStatisticsFragment(): StatisticsFragment

    @ContributesAndroidInjector
    abstract fun contributesPeriodicalTransactionsFragment(): PeriodicalTransactionsFragment
}
