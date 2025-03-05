package com.example.project_stay.ui.fragment

import android.content.Context
import android.content.Intent
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
import com.example.project_stay.databinding.FragmentProfileBinding
import com.example.project_stay.ui.activity.LoginActivity
import com.example.project_stay.ui.activity.PersonalDetailsActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        binding.btnPersonalDetails.setOnClickListener {
            val intent = Intent(
                this@ProfileFragment.requireContext(),
                PersonalDetailsActivity::class.java
            )
            startActivity(intent)
        }


    }

    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("hotel", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Clear saved user credentials and uncheck Remember Me
        editor.remove("email")
        editor.remove("password")
        editor.putBoolean("rememberMe", false) // Uncheck Remember Me
        editor.apply()

        auth.signOut() // Sign out the user

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close ProfileFragment and prevent going back

    }

}


