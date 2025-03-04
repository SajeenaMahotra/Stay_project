package com.example.project_stay.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.project_stay.databinding.FragmentHotelierProfileBinding
import com.example.project_stay.ui.activity.HotelDetailsActivity
import com.google.firebase.auth.FirebaseAuth

class HotelierProfileFragment : Fragment() {

    private lateinit var binding: FragmentHotelierProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewBinding
        binding = FragmentHotelierProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get the logged-in user ID
        val userId = auth.currentUser?.uid ?: ""

        // Set click listener for the "Hotel Details" button using binding
        binding.btnHotelDetails.setOnClickListener {
            // Open HotelDetailsActivity and pass the user ID
            val intent = Intent(requireContext(), HotelDetailsActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }
    }
}