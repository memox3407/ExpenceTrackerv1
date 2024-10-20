package com.example.expensetrackerv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val addExpenseButton = findViewById<Button>(R.id.addExpenseButton)

        val user = auth.currentUser
        user?.let {
            welcomeTextView.text = "Welcome, ${user.email}"
        }

        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}
