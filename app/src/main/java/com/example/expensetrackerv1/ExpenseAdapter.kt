package com.example.expensetrackerv1
// ExpenseAdapter.kt


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val expenseList: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.dateTextView.text = formatDate(expense.date)
        holder.descriptionTextView.text = expense.description
        holder.amountTextView.text = "%.2f".format(expense.amount) // Tutarı iki ondalık ile formatlama
    }

    override fun getItemCount() = expenseList.size

    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(timestamp)
    }
}
