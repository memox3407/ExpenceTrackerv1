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
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView) // Welcome TextView'i fonksiyonun başında tanımla

        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()
                var totalAmount = 0.0
                var currency = "" // Varsayılan olarak boş bırakıyoruz
                var isSingleCurrency = true // Tek bir para birimi kullanılıp kullanılmadığını kontrol etmek için

                for (expenseSnapshot in snapshot.children) {
                    val expense = expenseSnapshot.getValue(Expense::class.java)
                    expense?.let {
                        expenseList.add(it)
                        totalAmount += it.amount

                        // İlk harcamanın para birimi belirlenir
                        if (currency.isEmpty() && !it.currency.isNullOrEmpty()) {
                            currency = it.currency
                        } else if (currency != it.currency && !it.currency.isNullOrEmpty()) {
                            // Eğer bir harcamanın para birimi farklıysa, tek bir para birimi olmadığını işaretleriz
                            isSingleCurrency = false
                        }
                    }
                }

                expenseAdapter.notifyDataSetChanged()

                // Toplam tutarı dinamik olarak göster
                if (isSingleCurrency && currency.isNotEmpty()) {
                    // Tüm harcamalar aynı para birimindeyse
                    totalAmountTextView.text = "Total: %.2f %s".format(totalAmount, currency)
                } else if (!currency.isEmpty()) {
                    // Para birimi boş değilse ve birden fazla para birimi varsa
                    totalAmountTextView.text = "Total: %.2f (%s and Multiple Currencies)".format(totalAmount, currency)
                } else {
                    // Para birimi boşsa, sadece toplam tutarı göster
                    totalAmountTextView.text = "Total: %.2f".format(totalAmount)
                }

                // Eğer veri varsa, welcomeTextView'i gizle
                if (expenseList.isNotEmpty()) {
                    welcomeTextView.visibility = View.GONE
                } else {
                    welcomeTextView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to fetch expenses: ${error.message}")
            }
        })
    }



}
