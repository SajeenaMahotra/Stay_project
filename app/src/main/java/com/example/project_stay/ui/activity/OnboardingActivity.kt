package com.example.project_stay.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.project_stay.R
import com.example.project_stay.adapter.OnboardingAdapter
import com.example.project_stay.ui.OnboardingItem

class OnboardingActivity : AppCompatActivity() {

    private lateinit var indicators: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Initialize slides
        val items = listOf(
            OnboardingItem("Find Your \n Perfect Stay.", "Discover the best hotels\n at your fingertips.", R.drawable.slideone),
            OnboardingItem("Book in \n seconds!", "Fast, secure, and\n hassle-free bookings.", R.drawable.slidetwo),
            OnboardingItem("Travel Smarter,\n Stay Better.", "Enjoy premium stays\n and exciting perks.", R.drawable.slidethree)
        )

        // Setup ViewPager
        val adapter = OnboardingAdapter(items)
        val viewPager = findViewById<ViewPager2>(R.id.slideViewPager)
        viewPager.adapter = adapter

        // Setup indicators
        indicators = findViewById(R.id.indicatorContainer)
        setupIndicators(items.size)
        setCurrentIndicator(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)
            }
        })

        // Setup button actions
        findViewById<Button>(R.id.skipButton).setOnClickListener {
            finish() // Navigate to the main screen
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (viewPager.currentItem + 1 < adapter.itemCount) {
                viewPager.currentItem += 1
            } else {
                finish() // Navigate to the main screen
            }
        }
    }

    private fun setupIndicators(count: Int) {
        indicators.removeAllViews() // Ensure the container is cleared first
        val layoutParams = LinearLayout.LayoutParams(
            24, // Width in pixels (e.g., 24px or any desired size)
            24  // Height in pixels
        )
        layoutParams.setMargins(16, 0, 16, 0) // Adjust spacing between indicators

        repeat(count) {
            val imageView = ImageView(this)
            imageView.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
            )
            imageView.layoutParams = layoutParams
            indicators.addView(imageView)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicators.childCount
        for (i in 0 until childCount) {
            val imageView = indicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
                )
            }
        }
    }
}
