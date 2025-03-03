package com.example.project_stay.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.HotelAdapter
import com.example.project_stay.databinding.FragmentWishlistBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepository
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.repository.UserRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel
import com.example.project_stay.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


class WishlistFragment : Fragment() {
    lateinit var binding: FragmentWishlistBinding
    lateinit var userViewModel: UserViewModel
    lateinit var hotelViewModel: HotelViewModel
    lateinit var adapter: HotelAdapter
    private var userId: String? = null
    private lateinit var auth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        val hotelRepo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(hotelRepo)

        userId = FirebaseAuth.getInstance().currentUser?.uid

        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        adapter = HotelAdapter(requireContext(), arrayListOf(), userId ?: "", hotelRepo, hotelViewModel,auth)
        binding.wishlitedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.wishlitedRecyclerView.adapter = adapter

        userId?.let { uid ->
            hotelViewModel.getWishlistedHotels(uid) { hotels, success, message ->
                if (success) {
                    adapter.updateData(hotels ?: emptyList())
                } else {
                    Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        hotelViewModel.wishlistedHotels.observe(viewLifecycleOwner) { hotels ->
            val filteredHotels = hotels?.filter { it.isWishlisted } ?: emptyList()
            adapter.updateData(filteredHotels)
        }




    }
}


