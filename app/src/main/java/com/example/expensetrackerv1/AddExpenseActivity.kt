package com.example.expensetrackerv1



import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val amountEditText = findViewById<EditText>(R.id.amountEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val saveExpenseButton = findViewById<Button>(R.id.saveExpenseButton)

        // Kategori listesi, ilk eleman olarak bir hint içerir
        val categories = listOf("Kategori Seçiniz", "Food", "Transport", "Entertainment", "Health", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Para birimi listesini oluştur ve varsayılan değeri "TRY" olarak ayarla
        val currencies = listOf("USD", "TRY", "EUR", "GBP")
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = currencyAdapter

        // Varsayılan olarak "TRY" seçili olacak şekilde ayarla
        val defaultCurrencyIndex = currencies.indexOf("TRY")
        if (defaultCurrencyIndex >= 0) {
            currencySpinner.setSelection(defaultCurrencyIndex)
        }

        saveExpenseButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val category = categorySpinner.selectedItem.toString() // Kategori seçiminden alınan değer
            val description = descriptionEditText.text.toString()
            val selectedCurrency = currencySpinner.selectedItem.toString() // Para birimi seçiminden alınan değer

            // Kullanıcı bir kategori seçmemişse uyarı ver
            if (category == "Kategori Seçiniz") {
                Toast.makeText(this, "Lütfen bir kategori seçiniz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount != null && description.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                val expenseId = UUID.randomUUID().toString()

                // Expense nesnesini oluştur
                val expense = Expense(expenseId, amount, category, System.currentTimeMillis(), description, selectedCurrency)

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
