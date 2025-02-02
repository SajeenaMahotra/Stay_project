package com.example.project_stay.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_stay.ui.fragment.AllFragment
import com.example.project_stay.ui.fragment.NearbyFragment
import com.example.project_stay.ui.fragment.OffersFragment
import com.example.project_stay.ui.fragment.PopularFragment

class TabAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return AllFragment()
            1 -> return PopularFragment()
            2 -> return OffersFragment()
            3 -> return NearbyFragment()
            else -> return AllFragment()
        }
    }
}
