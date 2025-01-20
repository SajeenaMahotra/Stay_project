package com.example.project_stay.model

data class Amenity(
    val id: Int,
    val name: String,
    val iconResId: Int, // Resource ID for drawable icon
    var isSelected: Boolean = false // Whether the user has selected this amenity
)