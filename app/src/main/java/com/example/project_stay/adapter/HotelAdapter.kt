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
import com.example.project_stay.repository.HotelRepository
import com.example.project_stay.ui.activity.HotelInfoActivity
import com.example.project_stay.viewmodel.HotelViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class HotelAdapter(private val context: Context, private val hotelList: MutableList<Hotel>,private val userId: String,
                   private val repository: HotelRepository,  val hotelViewModel: HotelViewModel,private val auth: FirebaseAuth
) :
    RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelImage: ImageView = itemView.findViewById(R.id.hotelImage)
        val hotelName: TextView = itemView.findViewById(R.id.hotelName)
        val location: TextView = itemView.findViewById(R.id.location)
        val rating: TextView = itemView.findViewById(R.id.rating) // Add rating if applicable
        val price: TextView = itemView.findViewById(R.id.price)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val wishlistButton: ImageButton = itemView.findViewById(R.id.whishlistButton)

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

        val auth = FirebaseAuth.getInstance()


        // Fetch wishlist status for the current hotel
        val wishlistRef = FirebaseDatabase.getInstance().getReference("wishlists").child(userId)
        wishlistRef.child(hotel.hotelId).get().addOnSuccessListener { snapshot ->
            val isWishlisted = if (snapshot.value is Boolean) {
                snapshot.getValue(Boolean::class.java) ?: false
            } else if (snapshot.value is Map<*, *>) {
                (snapshot.value as Map<*, *>)["isWishlisted"] as? Boolean ?: false
            } else {
                false
            }
            hotel.isWishlisted = isWishlisted

            holder.wishlistButton.setImageResource(
                if (hotel.isWishlisted) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
            )
        }

        holder.wishlistButton.setOnClickListener {
            val newStatus = !hotel.isWishlisted
            hotelViewModel.addToWishList(userId, hotel.hotelId, newStatus) { success, message ->
                if (success) {
                    hotel.isWishlisted = newStatus
                    notifyItemChanged(position)
                    Toast.makeText(context, if (newStatus) "Added to Wishlist" else "Removed from Wishlist", Toast.LENGTH_SHORT).show()
//                    if (!newStatus) {
//                        hotelList.removeAt(position)
//                        notifyItemRemoved(position)
//                        notifyItemRangeChanged(position, hotelList.size)
//                    }
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
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

    fun updateData(newList: List<Hotel>) {
        hotelList.clear()
        hotelList.addAll(newList)
        notifyDataSetChanged()
    }
}
