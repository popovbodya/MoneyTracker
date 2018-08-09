package ru.popov.bodya.presentation.transactions

import ru.popov.bodya.domain.transactions.models.Transaction

/**
 *  @author popovbodya
 */
interface OnTransactionDeletedListener {

    fun onTransactionDeleted(transaction: Transaction)

}