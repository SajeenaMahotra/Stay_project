package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.project_stay.R

class EditDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_details)

        // Find the Spinner by ID
        val spinner: Spinner = findViewById(R.id.spinnerGender)

        // List of gender options
        val genderList = listOf("Male", "Female", "Other")

        // ArrayAdapter to populate the Spinner
        val adapter = ArrayAdapter(
            this, // Use `this` as the context in an Activity
            android.R.layout.simple_spinner_item, // Default dropdown layout
            genderList
        )

        // Set dropdown view resource for the adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Attach the adapter to the Spinner
        spinner.adapter = adapter
    }
}