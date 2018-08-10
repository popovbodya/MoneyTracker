package ru.popov.bodya.presentation.periodical.adapter

import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction

/**
 *  @author popovbodya
 */
interface OnPeriodicalTransactionDeletedListener {

    fun onPeriodicalTransactionDeleted(periodicalTransaction: PeriodicalTransaction)

}