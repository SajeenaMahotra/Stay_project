package com.example.yourapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_stay.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize all buttons and components
        val btnPersonalDetails = view.findViewById<LinearLayout>(R.id.btnPersonalDetails)

        val btnNotifications = view.findViewById<LinearLayout>(R.id.btnNotifications)
        val switchNotifications = view.findViewById<Switch>(R.id.switchNotifications)
        val btnPrivacyPolicy = view.findViewById<LinearLayout>(R.id.btnPrivacyPolicy)
        val btnTermsConditions = view.findViewById<LinearLayout>(R.id.btnTermsConditions)
        val btnAboutApp = view.findViewById<LinearLayout>(R.id.btnAboutApp)
        val btnHelpSupport = view.findViewById<LinearLayout>(R.id.btnHelpSupport)
        val btnFAQs = view.findViewById<LinearLayout>(R.id.btnFAQs)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Set click listeners for each button
        btnPersonalDetails.setOnClickListener {
            navigateToDetailsPage("Personal Details")
        }

        btnNotifications.setOnClickListener {
            switchNotifications.isChecked = !switchNotifications.isChecked // Toggle the switch
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Notifications ON" else "Notifications OFF"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        btnPrivacyPolicy.setOnClickListener {
            navigateToDetailsPage("Privacy Policy")
        }

        btnTermsConditions.setOnClickListener {
            navigateToDetailsPage("Terms and Conditions")
        }

        btnAboutApp.setOnClickListener {
            navigateToDetailsPage("About App")
        }

        btnHelpSupport.setOnClickListener {
            navigateToDetailsPage("Help and Support")
        }

        btnFAQs.setOnClickListener {
            navigateToDetailsPage("FAQs")
        }

        btnLogout.setOnClickListener {
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            // Handle logout logic here (e.g., clearing user session, navigating to login page)
        }

        return view
    }

    private fun navigateToDetailsPage(pageName: String) {
        Toast.makeText(context, "Navigating to $pageName", Toast.LENGTH_SHORT).show()
        // Add navigation logic here
        // For example: FindNavController().navigate(R.id.yourDestinationId)
    }
}


