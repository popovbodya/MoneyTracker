package ru.popov.bodya.domain.transactions.models

import ru.popov.bodya.domain.currency.model.Currency

/**
 *  @author popovbodya
 */
data class Transaction(val transactionId: Int,
                       val wallet: WalletType,
                       val currency: Currency,
                       val category: TransactionsCategory,
                       val amount: Double,
                       val timeCreated: Long,
                       val description: String)

