package com.example.expensetrackerv1


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var expenseList: ArrayList<Expense>
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var totalAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Expenses")

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val addExpenseButton = findViewById<Button>(R.id.addExpenseButton)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView)

        expenseRecyclerView.layoutManager = LinearLayoutManager(this)
        expenseList = ArrayList()
        expenseAdapter = ExpenseAdapter(expenseList)
        expenseRecyclerView.adapter = expenseAdapter

        val user = auth.currentUser
        user?.let {
            welcomeTextView.text = "Welcome, ${user.email}"
            fetchExpenses(it.uid)
        }

        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchExpenses(userId: String) {
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()
                var totalAmount = 0.0
                for (expenseSnapshot in snapshot.children) {
                    val expense = expenseSnapshot.getValue(Expense::class.java)
                    expense?.let {
                        expenseList.add(it)
                        totalAmount += it.amount // Toplam tutarı ekliyoruz
                    }
                }
                expenseAdapter.notifyDataSetChanged()

                // Eğer veri varsa, welcomeTextView'i gizle
                val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
                if (expenseList.isNotEmpty()) {
                    welcomeTextView.visibility = View.GONE
                } else {
                    welcomeTextView.visibility = View.VISIBLE
                }

                totalAmountTextView.text = "Total: $%.2f".format(totalAmount) // Toplam tutarı göster
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to fetch expenses: ${error.message}")
            }
        })
    }

}
