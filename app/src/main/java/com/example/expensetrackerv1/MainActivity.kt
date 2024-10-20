package com.example.expensetrackerv1


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase Authentication örneğini al
        auth = FirebaseAuth.getInstance()

        // Kullanıcı oturum kontrolü yap
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Eğer oturum açmış kullanıcı yoksa LoginActivity'ye yönlendir
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // MainActivity'yi kapat
            return
        }

        // Eğer kullanıcı oturumu varsa, ana ekrana devam et
        setContentView(R.layout.activity_main)

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        welcomeTextView.text = "Welcome, ${currentUser.email}"
    }
}
