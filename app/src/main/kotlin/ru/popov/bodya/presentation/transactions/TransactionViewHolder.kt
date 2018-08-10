package ru.popov.bodya.presentation.transactions

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import kotlinx.android.synthetic.main.item_transaction.view.*
import ru.popov.bodya.R
import ru.popov.bodya.domain.transactions.models.Transaction
import ru.popov.bodya.domain.transactions.models.TransactionsCategory
import ru.popov.bodya.util.ResourcesSelector

/**
 *
 * @author Popov Bogdan
 */
class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Transaction) = with(itemView) {
        transaction_description_text_view.text = item.description
        date_text_view.text = DateFormat.format("dd.MM.yyyy, HH:mm", item.timeCreated)
        currency_text_view.text = item.currency.toString()
        transaction_category_text_view.text = itemView.resources.getString(ResourcesSelector.fromTransactionCategoryToString(item.category))
        currency_type_image_view.setImageResource(ResourcesSelector.fromTransactionCategoryToDrawable(item.category))

        if (item.category is TransactionsCategory.ExpenseTransactionsCategory) {
            tv_amount.setBaseColor(itemView.resources.getColor(R.color.colorExpense))
            tv_amount.setDecimalsColor(itemView.resources.getColor(R.color.colorExpense))
        } else {
            tv_amount.setBaseColor(itemView.resources.getColor(R.color.colorIncome))
            tv_amount.setDecimalsColor(itemView.resources.getColor(R.color.colorIncome))
        }
        tv_amount.amount = item.amount.toFloat()
        tv_amount.setSymbol("")
    }
}