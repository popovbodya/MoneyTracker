package ru.popov.bodya.domain.statistics

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.rgb
import io.reactivex.Single
import ru.popov.bodya.R
import ru.popov.bodya.core.resources.ResourceManager
import ru.popov.bodya.data.repositories.CurrenciesRepository
import ru.popov.bodya.domain.currency.model.Currency
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.presentation.common.translatedNameId


/**
 *  @author popovbodya
 */
class StatisticsInteractor(private val currenciesRepository: CurrenciesRepository,
                           private val resourceManager: ResourceManager) {

    fun createPieDataSetBasedOnTransactionCategoriesSingle(transactionList: List<Transaction>): Single<PieData> =
            Single.fromCallable { createPieDataSetBasedOnTransactionCategories(transactionList) }

    private fun createPieDataSetBasedOnTransactionCategories(transactionList: List<Transaction>): PieData {
        val entriesList: MutableList<PieEntry> = mutableListOf()
        val categoriesMap = getTransactionCategoriesMap(transactionList)
        categoriesMap.entries.forEach { (category, amount) ->
            entriesList.add(PieEntry(amount.toFloat(), category))
        }
        val pieDataSet = PieDataSet(entriesList, resourceManager.getString(R.string.statistics))
        pieDataSet.valueTextSize = 12F
        pieDataSet.valueTextColor = rgb("#FFFFFF")
        pieDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        return PieData(pieDataSet)
    }

    private fun getTransactionCategoriesMap(transactionList: List<Transaction>): Map<String, Double> {
        val transactionCategoriesMap = mutableMapOf<String, Double>()
        for (transaction in transactionList) {
            val name = when (transaction.category) {
                is TransactionsCategory.ExpenseTransactionsCategory -> resourceManager.getString(transaction.category.translatedNameId())
                is TransactionsCategory.IncomeTransactionsCategory -> resourceManager.getString(transaction.category.translatedNameId())
            }
            val currentAmount = transactionCategoriesMap[name]
            if (currentAmount == null) {
                transactionCategoriesMap[name] = getOperationAmount(transaction)
            } else {
                transactionCategoriesMap[name] = currentAmount + getOperationAmount(transaction)
            }
        }
        return transactionCategoriesMap
    }

    private fun getOperationAmount(transaction: Transaction): Double {
        return when (transaction.currency) {
            Currency.USD -> (1.toDouble() / currenciesRepository.getCachedExchangeRate().usd) * transaction.amount
            Currency.EUR -> (1.toDouble() / currenciesRepository.getCachedExchangeRate().eur) * transaction.amount
            Currency.RUB -> transaction.amount
        }
    }
}