package com.example.project_stay.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityHotelierNavigationBinding
import com.example.project_stay.ui.fragment.BookingFragment
import com.example.project_stay.ui.fragment.HomeFragment
import com.example.project_stay.ui.fragment.HotelierBookingFragment
import com.example.project_stay.ui.fragment.HotelierChatFragment
import com.example.project_stay.ui.fragment.HotelierHomeFragment
import com.example.project_stay.ui.fragment.HotelierProfileFragment
import com.example.project_stay.ui.fragment.WishlistFragment
import com.example.yourapp.ProfileFragment

class HotelierNavigationActivity : AppCompatActivity() {
    lateinit var binding: ActivityHotelierNavigationBinding

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager =supportFragmentManager
        val fragmentTransaction : FragmentTransaction =fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.hotelierFrameDashboard,fragment)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHotelierNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HotelierHomeFragment())
        binding.hotelierNavBar.setOnItemSelectedListener { menu ->
            when(menu.itemId){
                R.id.hotelierHome -> replaceFragment(HotelierHomeFragment())
                    R.id.hotelierBookings -> replaceFragment(HotelierBookingFragment())
                        R.id.hotelierMessages -> replaceFragment(HotelierChatFragment())
                            R.id.hotelierProfile -> replaceFragment(HotelierProfileFragment())
                                else -> {}
            }
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}








