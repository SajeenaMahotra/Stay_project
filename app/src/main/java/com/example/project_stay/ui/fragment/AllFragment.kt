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
import com.example.project_stay.databinding.FragmentAllBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepository
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel
import com.google.firebase.auth.FirebaseAuth

class AllFragment : Fragment() {
    private lateinit var binding: FragmentAllBinding
    private lateinit var viewModel: HotelViewModel
    private lateinit var hotelRecyclerView: RecyclerView
    private lateinit var hotelList: ArrayList<Hotel>
    private lateinit var adapter: HotelAdapter
    private lateinit var repository: HotelRepository
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        hotelRecyclerView = view.findViewById(R.id.recyclerView)
        hotelRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        hotelList = ArrayList()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        repository = HotelRepositoryImpl()
        viewModel = HotelViewModel(repository)

        auth = FirebaseAuth.getInstance()

        adapter = HotelAdapter(requireContext(), hotelList,userId,repository,viewModel, auth)
        hotelRecyclerView.adapter = adapter


        viewModel.getHotels().observe(viewLifecycleOwner, Observer { hotels ->
            hotelList.clear()
            hotelList.addAll(hotels)
            adapter.notifyDataSetChanged()
        })

        return view
    }

}