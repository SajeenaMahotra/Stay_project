package com.example.project_stay.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.model.Hotel
import com.example.project_stay.ui.activity.HotelInfoActivity
import com.squareup.picasso.Picasso

class HotelAdapter(private val context: Context, private val hotelList: List<Hotel>) :
    RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelImage: ImageView = itemView.findViewById(R.id.hotelImage)
        val hotelName: TextView = itemView.findViewById(R.id.hotelName)
        val location: TextView = itemView.findViewById(R.id.location)
        val rating: TextView = itemView.findViewById(R.id.rating) // Add rating if applicable
        val price: TextView = itemView.findViewById(R.id.price)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hotel_card, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotelList[position]

        holder.hotelName.text = hotel.name
        holder.location.text = hotel.location
        holder.price.text = "£${hotel.lowestPrice}" // Format price

        // Load hotel image using Picasso
        //Picasso.get().load(hotel.imageUrl).into(holder.hotelImage)
        if (!hotel.imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(hotel.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.hotelImage)
        } else {
            // Set a default image if no URL is available
            holder.hotelImage.setImageResource(R.drawable.placeholder)
        }
        // Open HotelDetailsActivity when card is clicked
        holder.cardView.setOnClickListener {
            val intent = Intent(context, HotelInfoActivity::class.java)
            intent.putExtra("hotelId", hotel.hotelId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return hotelList.size
    }
}
