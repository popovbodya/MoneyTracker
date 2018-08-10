package ru.popov.bodya.presentation.statistics.model

import ru.popov.bodya.domain.transactions.models.WalletType

/**
 *  @author popovbodya
 */
data class StatisticsInitialData(val currentTime: Long, val currentWallet: WalletType, val isIncome: Boolean)