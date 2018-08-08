package ru.popov.bodya.domain.transactions

import io.reactivex.Completable
import io.reactivex.Single
import ru.popov.bodya.data.repositories.PeriodicalTransactionsRepository
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 * @author popovbodya
 */
class PeriodicalTransactionsInteractor(private val periodicalTransactionsRepository: PeriodicalTransactionsRepository) {

    companion object {
        const val DEFAULT_ID: Long = 0
    }

    fun getAllPeriodicalTransactionsByWallet(walletType: WalletType): Single<List<PeriodicalTransaction>> {
        return periodicalTransactionsRepository.getAllPeriodicalTransactionsByWallet(walletType)
    }

    fun createTransaction(selectedWallet: WalletType, selectedCategory: TransactionsCategory, selectedCurrency: Currency, amount: Double, time: Long, comment: String, period: Long): Completable {
        return periodicalTransactionsRepository.addPeriodicalTransaction(PeriodicalTransaction(DEFAULT_ID, selectedWallet, selectedCurrency, selectedCategory, amount, time, comment, period))
    }

    fun createTransactionListBasedOnPeriodicalTransactions(periodicalTransactionList: List<PeriodicalTransaction>, currentTimeInMillis: Long): List<Transaction> {
        val transactionList = mutableListOf<Transaction>()
        periodicalTransactionList.forEach {
            val count = (currentTimeInMillis - it.timeCreated) / it.period
            var transactionTime = it.timeCreated
            for (i in 0 until count) {
                transactionList.add(Transaction(TransactionsInteractor.DEFAULT_ID, it.wallet, it.currency, it.category, it.amount, transactionTime, it.description))
                transactionTime += it.period
            }
            periodicalTransactionsRepository.modifyPeriodicalTransactionTimeUpdated(it.id, transactionTime)
        }
        return transactionList
    }
}