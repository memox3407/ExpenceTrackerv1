package com.example.expensetrackerv1


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Expenses")

        val amountEditText = findViewById<EditText>(R.id.amountEditText)
        val categoryEditText = findViewById<EditText>(R.id.categoryEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val saveExpenseButton = findViewById<Button>(R.id.saveExpenseButton)

        saveExpenseButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val category = categoryEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (amount != null && category.isNotEmpty() && description.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                val expenseId = UUID.randomUUID().toString()

                val expense = Expense(expenseId, amount, category, System.currentTimeMillis(), description)

                userId?.let {
                    database.child(it).child(expenseId).setValue(expense).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                            finish() // Aktiviteyi kapat
                        } else {
                            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
