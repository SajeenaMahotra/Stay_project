package com.example.project_stay.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.project_stay.R
import com.example.project_stay.databinding.FragmentHomeBinding
import com.example.project_stay.adapter.TabAdapter
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    val tabTitle = arrayListOf("All", "Popular", "Offers", "Nearby")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.tabLayout.bringToFront()
        setUpTabLayoutWithViewPager()
        return binding.root
    }

    lateinit var adapter: TabAdapter

    private fun setUpTabLayoutWithViewPager() {

        val fragmentManager: FragmentManager = childFragmentManager
        adapter = TabAdapter(fragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab, position ->
            tab.text = tabTitle[position]
        }.attach()

        for (i in 0..3){
            val textView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
                as TextView
            textView.text = tabTitle[i]
            binding.tabLayout.getTabAt(i)?.customView = textView
        }
    }


}