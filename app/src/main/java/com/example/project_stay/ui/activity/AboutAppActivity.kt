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

class AboutAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_app)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.aboutAppContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvAbout = findViewById<TextView>(R.id.tvAbout)
        val btnOk = findViewById<Button>(R.id.btnOk)

        tvAbout.text = """
            About Stay
            
            Stay is a seamless hotel booking app that lets you find and reserve accommodations with ease. Whether you're looking for budget-friendly stays or luxury hotels, Stay provides personalized recommendations, secure bookings, and real-time availability.
            
            Features:
            - Quick and secure hotel bookings
            - Exclusive deals and discounts
            - User-friendly interface
            - 24/7 customer support
            
            Version: 1.0.0
            Developed by: Stay Team
            
            Thank you for choosing Stay!
        """.trimIndent()

        btnOk.setOnClickListener {
            Toast.makeText(this, "Thank you for using Stay!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
