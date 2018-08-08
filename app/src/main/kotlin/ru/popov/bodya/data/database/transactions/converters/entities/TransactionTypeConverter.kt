package ru.popov.bodya.data.database.transactions.converters.entities

import android.arch.persistence.room.TypeConverter
import ru.popov.bodya.domain.transactions.models.ExpenseCategory.*
import ru.popov.bodya.domain.transactions.models.IncomeCategory.*
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.domain.transactions.models.TransactionsCategory.ExpenseTransactionsCategory
import ru.popov.bodya.domain.transactions.models.TransactionsCategory.IncomeTransactionsCategory

class TransactionTypeConverter {

    @TypeConverter
    fun fromTransactionTypeToInt(transactionsCategory: TransactionsCategory): String =
            when (transactionsCategory) {
                is ExpenseTransactionsCategory -> transactionsCategory.expenseCategory.name
                is TransactionsCategory.IncomeTransactionsCategory -> transactionsCategory.incomeCategory.name
            }

    @TypeConverter
    fun fromIntToTransactionType(type: String): TransactionsCategory {
        return when (type) {
            FOOD.name -> ExpenseTransactionsCategory(FOOD)
            HOME.name -> ExpenseTransactionsCategory(HOME)
            COMMUNAL_PAYMENTS.name -> ExpenseTransactionsCategory(COMMUNAL_PAYMENTS)
            EDUCATION.name -> ExpenseTransactionsCategory(EDUCATION)
            CLOTHES.name -> ExpenseTransactionsCategory(CLOTHES)
            FAMILY.name -> ExpenseTransactionsCategory(FAMILY)
            REST.name -> ExpenseTransactionsCategory(REST)
            TREATMENT.name -> ExpenseTransactionsCategory(TREATMENT)
            OTHER_EXPENSE.name -> ExpenseTransactionsCategory(OTHER_EXPENSE)
            SALARY.name -> IncomeTransactionsCategory(SALARY)
            TRANSFER.name -> IncomeTransactionsCategory(TRANSFER)
            GIFT.name -> IncomeTransactionsCategory(GIFT)
            OTHER_INCOME.name -> IncomeTransactionsCategory(OTHER_INCOME)
            else -> ExpenseTransactionsCategory(OTHER_EXPENSE)
        }
    }
}