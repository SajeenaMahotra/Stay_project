package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_help)
<<<<<<< HEAD


=======
        // Set up window insets for edge-to-edge UI
>>>>>>> b3186c9a67b4e46449051bee6a051ed17cca99da
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
<<<<<<< HEAD

        val spinner: Spinner = findViewById(R.id.spinner2)
        val options = listOf("Refund", "Payment issue", "Account help", "General inquiry")

=======
        // Spinner setup
        val spinner: Spinner = findViewById(R.id.spinner2)
        val options = listOf("Refund", "Payment issue", "Account help", "General inquiry")
        // Create an ArrayAdapter using the options list and a default spinner layout
>>>>>>> b3186c9a67b4e46449051bee6a051ed17cca99da
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            options
        )
<<<<<<< HEAD

=======
        // Set the adapter to the spinner
>>>>>>> b3186c9a67b4e46449051bee6a051ed17cca99da
        spinner.adapter = adapter
    }
}
