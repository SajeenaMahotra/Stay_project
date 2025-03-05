package com.example.project_stay.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.model.RoomModel

class RoomListAdapter(
    private val rooms: List<RoomModel>,
    private val onItemClickListener: (RoomModel) -> Unit
) : RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_list, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.roomName.text = room.roomName
        holder.roomGuests.text = "Guests: ${room.numberOfGuests}"
        holder.roomPrice.text = "Price: £${room.pricePerNight}/night"

        // Highlight selected room
        holder.itemView.isSelected = selectedPosition == position

        // Set click listener
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged() // Refresh RecyclerView
            onItemClickListener(room)
        }
    }

    override fun getItemCount(): Int = rooms.size

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.roomName)
        val roomGuests: TextView = itemView.findViewById(R.id.roomGuests)
        val roomPrice: TextView = itemView.findViewById(R.id.roomPrice)
    }
}