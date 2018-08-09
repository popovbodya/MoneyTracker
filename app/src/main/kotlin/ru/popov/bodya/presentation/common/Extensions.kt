package ru.popov.bodya.presentation.common

import com.lounah.wallettracker.R
import ru.popov.bodya.domain.transactions.models.ExpenseCategory.*
import ru.popov.bodya.domain.transactions.models.IncomeCategory.*
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.WalletType
import ru.popov.bodya.domain.transactions.models.WalletType.*

/**
 *  @author popovbodya
 */

fun TransactionsCategory.ExpenseTransactionsCategory.translatedNameId() = when (this.expenseCategory) {
    AUTO -> R.string.auto
    TREATMENT -> R.string.treatment
    REST -> R.string.rest
    FAMILY -> R.string.family
    CLOTHES -> R.string.clothes
    EDUCATION -> R.string.education
    COMMUNAL_PAYMENTS -> R.string.communal_payments
    HOME -> R.string.home
    FOOD -> R.string.food
    OTHER_EXPENSE -> R.string.other
}

fun TransactionsCategory.IncomeTransactionsCategory.translatedNameId() = when (this.incomeCategory) {
    GIFT -> R.string.gift
    OTHER_INCOME -> R.string.other
    SALARY -> R.string.salary
    TRANSFER -> R.string.transfer
}

fun WalletType.translatedNameId() = when (this) {
    CREDIT_CARD -> R.string.credit_card
    BANK_ACCOUNT -> R.string.bank_account
    CASH -> R.string.cash
}

fun TransactionsCategory.ExpenseTransactionsCategory.drawableId() = when (this.expenseCategory) {
    AUTO -> R.drawable.ic_auto
    TREATMENT -> R.drawable.ic_treatment
    REST -> R.drawable.ic_rest
    FAMILY -> R.drawable.ic_family
    CLOTHES -> R.drawable.ic_clothes
    EDUCATION -> R.drawable.ic_education
    COMMUNAL_PAYMENTS -> R.drawable.ic_communal_payments
    HOME -> R.drawable.ic_home
    FOOD -> R.drawable.ic_food
    OTHER_EXPENSE -> R.drawable.ic_other
}