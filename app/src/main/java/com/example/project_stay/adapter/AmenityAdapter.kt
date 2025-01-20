package com.example.project_stay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.model.Amenity
import com.google.firebase.database.DatabaseReference

class AmenityAdapter(
    private val amenities: List<Amenity>,
    private val userId: String,
    private val database: DatabaseReference,
    private val onAmenityClick: (Amenity) -> Unit
) : RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder>() {

    inner class AmenityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.amenityIcon)
        val name: TextView = view.findViewById(R.id.amenityName)
        val container: ConstraintLayout = view.findViewById(R.id.amenityContainer)

        fun bind(amenity: Amenity) {
            icon.setImageResource(amenity.iconResId)
            name.text = amenity.name

            // Highlight selection
            icon.background = if (amenity.isSelected) {
                ContextCompat.getDrawable(itemView.context, R.drawable.selected_amenity)
            } else {
                ContextCompat.getDrawable(itemView.context, R.drawable.unselected_amenity)
            }

            // Click to toggle selection
            itemView.setOnClickListener {
                amenity.isSelected = !amenity.isSelected
                notifyItemChanged(adapterPosition)
                saveSelectionToFirebase(amenity)
                onAmenityClick(amenity)
            }
        }

        private fun saveSelectionToFirebase(amenity: Amenity) {
            val amenityRef = database.child(userId).child("amenities").child(amenity.id.toString())
            amenityRef.setValue(amenity.isSelected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmenityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_amenity, parent, false)
        return AmenityViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmenityViewHolder, position: Int) {
        holder.bind(amenities[position])
    }

    override fun getItemCount(): Int = amenities.size
}