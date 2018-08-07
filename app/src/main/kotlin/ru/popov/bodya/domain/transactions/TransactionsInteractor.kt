package ru.popov.bodya.domain.transactions

import io.reactivex.Completable
import io.reactivex.Single
import ru.popov.bodya.data.repositories.TransactionsRepository
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 * @author popovbodya
 */
class TransactionsInteractor(private val transactionsRepository: TransactionsRepository) {

    companion object {
        const val DEFAULT_ID = 0
    }

    fun getAllTransactionsByWallet(walletType: WalletType): Single<List<Transaction>> =
            transactionsRepository.getAllTransactionsByWallet(walletType)

    fun getIncomeTransactionsByWallet(walletType: WalletType): Single<List<Transaction>> =
            transactionsRepository.getIncomeTransactionsByWallet(walletType)

    fun getExpenseTransactionsByWallet(walletType: WalletType): Single<List<Transaction>> =
            transactionsRepository.getExpenseTransactionsByWallet(walletType)

    fun addTransaction(selectedWallet: WalletType, selectedCategory: TransactionsCategory, selectedCurrency: Currency, amount: Double, time: Long, comment: String): Completable {
        return transactionsRepository.addTransaction(Transaction(DEFAULT_ID, selectedWallet, selectedCurrency, selectedCategory, amount, time, comment))
    }

    fun addTransactionList(transactionList: List<Transaction>) {
        transactionsRepository.addTransactionList(transactionList)
    }

    fun getTransactionsPair(transactionList: List<Transaction>): Pair<List<Transaction>, List<Transaction>> {
        val incomeList: MutableList<Transaction> = mutableListOf()
        val exposeList: MutableList<Transaction> = mutableListOf()
        transactionList.forEach { transaction ->
            when (transaction.category) {
                is TransactionsCategory.Expense -> exposeList.add(transaction)
                is TransactionsCategory.Income -> incomeList.add(transaction)
            }
        }
        return Pair(incomeList, exposeList)
    }

    fun getPairDiff(pair: Pair<Double, Double>): Double = pair.first - pair.second



}