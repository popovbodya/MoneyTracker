package ru.popov.bodya.data.repositories

import io.reactivex.Completable
import io.reactivex.Single
import ru.popov.bodya.data.database.transactions.converters.models.TransactionEntityConverter
import ru.popov.bodya.data.database.transactions.dao.TransactionsDao
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 *  @author popovbodya
 */
class TransactionsRepository(private val transactionsDao: TransactionsDao,
                             private val transactionsEntityConverter: TransactionEntityConverter) {

    fun getAllTransactionsByWallet(walletType: WalletType): Single<List<Transaction>> =
            Single.fromCallable {
                transactionsDao.getAllTransactionsByWallet(walletType)
                        .map { transactionsEntityConverter.convert(it) }
            }

    fun getIncomeTransactionsByWallet(walletType: WalletType): Single<List<Transaction>> =
            Single.fromCallable {
                transactionsDao.getIncomeTransactionsByWallet(walletType)
                        .map { transactionsEntityConverter.convert(it) }
            }

    fun getExpenseTransactionsByWallet(walletType: WalletType): Single<List<Transaction>> =
            Single.fromCallable {
                transactionsDao.getExpenseTransactionsByWallet(walletType)
                        .map { transactionsEntityConverter.convert(it) }
            }

    fun getTransactionsByWalletAndDate(walletType: WalletType, startDate: Long, endDate: Long): Single<List<Transaction>> =
            Single.fromCallable {
                transactionsDao.getAllTransactionsByWalletAndDate(walletType, startDate, endDate)
                        .map { transactionsEntityConverter.convert(it) }
            }

    fun getIncomeTransactionsByWalletAndDate(walletType: WalletType, startDate: Long, endDate: Long): Single<List<Transaction>> =
            Single.fromCallable {
                transactionsDao.getIncomeTransactionsByWalletAndDate(walletType, startDate, endDate)
                        .map { transactionsEntityConverter.convert(it) }
            }

    fun getExpenseTransactionsByWalletAndDate(walletType: WalletType, startDate: Long, endDate: Long): Single<List<Transaction>> =
            Single.fromCallable {
                transactionsDao.getExpenseTransactionsByWalletAndDate(walletType, startDate, endDate)
                        .map { transactionsEntityConverter.convert(it) }
            }

    fun addTransaction(transaction: Transaction): Completable {
        return Completable.fromAction { transactionsDao.insert(transactionsEntityConverter.reverse(transaction)) }
    }

    fun removeTransaction(transaction: Transaction): Completable {
        return Completable.fromAction { transactionsDao.deleteTransaction(transaction.transactionId) }
    }

    fun addTransactionList(transactionList: List<Transaction>) {
        transactionsDao.insertAll(transactionsEntityConverter.reverseList(transactionList))
    }
}