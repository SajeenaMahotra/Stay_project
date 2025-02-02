package com.example.project_stay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.model.RoomModel

class RoomsAdapter(
    private val roomsList: List<RoomModel>,
    private val onRoomClick: (RoomModel) -> Unit
) : RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>() {

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)
        val roomDetailsTextView: TextView = itemView.findViewById(R.id.roomDetailsTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomsList[position]
        holder.roomNameTextView.text = room.roomName
        holder.roomDetailsTextView.text =
            "Rooms: ${room.numberOfRooms} | Guests: ${room.numberOfGuests} | Price: $${room.pricePerNight}/night"

        // Handle card click
        holder.cardView.setOnClickListener {
            onRoomClick(room)
        }
    }

    override fun getItemCount(): Int = roomsList.size
}

