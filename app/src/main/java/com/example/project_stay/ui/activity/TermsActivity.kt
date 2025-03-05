package com.example.project_stay.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terms)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.termsContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvTerms = findViewById<TextView>(R.id.tvTerms)
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnDecline = findViewById<Button>(R.id.btnDecline)

        tvTerms.text = """
            Welcome to Stay! By using our services, you agree to the following terms and conditions:
            
            1. All bookings are subject to availability.
            2. Cancellations within 24 hours of check-in may be subject to a fee.
            3. Guests must provide a valid ID at check-in.
            4. The hotel reserves the right to refuse service to anyone.
            5. Use of the Stay app implies agreement to our privacy policy.
            
            Please read carefully before proceeding.
        """.trimIndent()

        btnAccept.setOnClickListener {
            Toast.makeText(this, "You have accepted the Terms & Conditions.", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnDecline.setOnClickListener {
            Toast.makeText(this, "You must accept the Terms to proceed.", Toast.LENGTH_SHORT).show()
        }
    }
}
