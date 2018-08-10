package ru.popov.bodya.presentation.periodical.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.popov.bodya.R
import ru.popov.bodya.domain.transactions.models.PeriodicalTransaction

/**
 *  @author popovbodya
 */
class PeriodicalTransactionsAdapter(private val onPeriodicalTransactionDeletedListener: OnPeriodicalTransactionDeletedListener) : RecyclerView.Adapter<PeriodicalTransactionViewHolder>() {

    private val periodicalTransactions = mutableListOf<PeriodicalTransaction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodicalTransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.periodical_transaction_item, parent, false)
        return PeriodicalTransactionViewHolder(view)
    }

    override fun getItemCount() = periodicalTransactions.size

    override fun onBindViewHolder(holder: PeriodicalTransactionViewHolder, position: Int) {
        val action = periodicalTransactions[position]
        holder.bind(action)
    }

    fun removeItem(position: Int) {
        val transaction = periodicalTransactions[position]
        periodicalTransactions.removeAt(position)
        notifyItemRemoved(position)
        onPeriodicalTransactionDeletedListener.onPeriodicalTransactionDeleted(transaction)
    }

    fun updateDataSet(transactionList: List<PeriodicalTransaction>) {
        this.periodicalTransactions.clear()
        periodicalTransactions.addAll(transactionList)
        notifyDataSetChanged()
    }
}