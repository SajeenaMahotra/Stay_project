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
import com.example.project_stay.databinding.ActivityLoginBinding
import com.example.project_stay.databinding.ActivityNavigationBinding
import com.example.project_stay.ui.fragment.BookingFragment
import com.example.project_stay.ui.fragment.HomeFragment
import com.example.project_stay.ui.fragment.WishlistFragment
import com.example.project_stay.ui.fragment.ProfileFragment

class NavigationActivity : AppCompatActivity() {
    lateinit var binding: ActivityNavigationBinding

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager: FragmentManager =supportFragmentManager
        val fragmentTransaction :FragmentTransaction =fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameDashboard,fragment)
        fragmentTransaction.commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding =ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Check if an intent extra is passed to load a specific fragment
        val fragmentToLoad = intent.getStringExtra("FRAGMENT_TO_LOAD")
        if (fragmentToLoad == "ProfileFragment") {
            replaceFragment(ProfileFragment())
        } else {
            replaceFragment(HomeFragment())
        }
        binding.bottomNavbar.setOnItemSelectedListener { menu ->
            when(menu.itemId){
                R.id.navHome -> replaceFragment(HomeFragment())
                    R.id.navFavorite -> replaceFragment(WishlistFragment())
                        R.id.navBookings -> replaceFragment(BookingFragment())
                            R.id.navProfile -> replaceFragment(ProfileFragment())
                                else ->{}
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