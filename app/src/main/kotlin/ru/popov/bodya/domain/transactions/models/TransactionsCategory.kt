package ru.popov.bodya.domain.transactions.models

/**
 *  @author popovbodya
 */
sealed class TransactionsCategory {
    class ExpenseTransactionsCategory(val expenseCategory: ExpenseCategory) : TransactionsCategory()
    class IncomeTransactionsCategory(val incomeCategory: IncomeCategory) : TransactionsCategory()
}
