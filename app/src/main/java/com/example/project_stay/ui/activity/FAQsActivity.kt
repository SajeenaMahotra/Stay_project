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
import com.example.project_stay.databinding.ActivityFaqsBinding

class FAQsActivity : AppCompatActivity() {
    lateinit var binding:ActivityFaqsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faqs)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.faqsContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvFAQs = findViewById<TextView>(R.id.tvFAQs)
        val btnOk = findViewById<Button>(R.id.btnOk)

        tvFAQs.text = """
            Frequently Asked Questions (FAQs)
            
            Q1: How do I book a hotel with Stay?
            A: Simply search for your destination, select a hotel, choose your room, and confirm your booking.

            Q2: Can I cancel my booking?
            A: Yes, cancellations are available under certain conditions. Please check the cancellation policy before booking.

            Q3: Is my payment information secure?
            A: Absolutely! Stay uses encrypted transactions to ensure your payment details are secure.

            Q4: Can I modify my booking?
            A: Yes, modifications are possible but depend on hotel policies. Contact support for assistance.

            Q5: How do I contact customer support?
            A: You can reach us via the support section in the app or email us at support@stayapp.com.

            Need more help? Contact us anytime!
        """.trimIndent()

        btnOk.setOnClickListener {
            Toast.makeText(this, "Thanks for checking the FAQs!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
