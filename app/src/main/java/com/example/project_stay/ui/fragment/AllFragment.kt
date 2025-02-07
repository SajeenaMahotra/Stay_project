package com.example.project_stay.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.HotelAdapter
import com.example.project_stay.databinding.FragmentAllBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel

class AllFragment : Fragment() {
    private lateinit var binding: FragmentAllBinding
    private lateinit var viewModel: HotelViewModel
    private lateinit var hotelRecyclerView: RecyclerView
    private lateinit var hotelList: ArrayList<Hotel>
    private lateinit var adapter: HotelAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        hotelRecyclerView = view.findViewById(R.id.recyclerView)
        hotelRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        hotelList = ArrayList()
        adapter = HotelAdapter(requireContext(), hotelList)
        hotelRecyclerView.adapter = adapter

        val repository = HotelRepositoryImpl()
        viewModel = HotelViewModel(repository)

        // Observe LiveData
        viewModel.getHotels().observe(viewLifecycleOwner, Observer { hotels ->
            hotelList.clear()
            hotelList.addAll(hotels)
            adapter.notifyDataSetChanged()
        })

        return view
    }
}