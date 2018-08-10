package ru.popov.bodya.domain.templates.model

import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 *
 * @author Popov Bogdan
 */
data class Template(
        val name: String,
        val wallet: WalletType,
        val currency: Currency,
        val category: TransactionsCategory,
        val amount: Double,
        val timeCreated: Long,
        val description: String)
