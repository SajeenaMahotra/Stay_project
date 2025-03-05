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

class PrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_privacy)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.privacyContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvPrivacy = findViewById<TextView>(R.id.tvPrivacy)
        val btnAgree = findViewById<Button>(R.id.btnAgree)
        val btnDisagree = findViewById<Button>(R.id.btnDisagree)

        tvPrivacy.text = """
            Privacy Policy - Stay App
            
            1. We collect personal information to provide better booking services.
            2. Your data will not be shared with third parties without consent.
            3. Payment details are securely processed and not stored by Stay.
            4. Location access is required for better recommendations.
            5. By using Stay, you agree to the terms outlined in this policy.

            Please review and accept the policy to continue.
        """.trimIndent()

        btnAgree.setOnClickListener {
            Toast.makeText(this, "You have accepted the Privacy Policy.", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnDisagree.setOnClickListener {
            Toast.makeText(this, "You must accept the Privacy Policy to proceed.", Toast.LENGTH_SHORT).show()
        }
    }
}
