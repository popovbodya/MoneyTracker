package ru.popov.bodya.data.database.transactions.converters.models

import ru.popov.bodya.core.converter.Converter
import ru.popov.bodya.data.database.transactions.entities.TransactionEntity
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory

/**
 *  @author popovbodya
 */
class TransactionEntityConverter : Converter<TransactionEntity, Transaction> {

    override fun reverse(to: Transaction): TransactionEntity {
        val amount = when (to.category) {
            is TransactionsCategory.ExpenseTransactionsCategory -> -1 * to.amount
            is TransactionsCategory.IncomeTransactionsCategory -> to.amount
        }
        return TransactionEntity(0, to.wallet, to.currency, to.category, amount, to.timeCreated, to.description)
    }

    override fun convert(from: TransactionEntity): Transaction {
        val amount = Math.abs(from.amount)
        return Transaction(from.id, from.wallet, from.currency, from.category, amount, from.timeCreated, from.description)
    }

    fun reverseList(transactionsList: List<Transaction>): List<TransactionEntity> {
        val transactionEntityList: MutableList<TransactionEntity> = mutableListOf()
        transactionsList.forEach { transactionEntityList.add(reverse(it)) }
        return transactionEntityList
    }
}