package com.example.project_stay.repository

import com.example.project_stay.R
import com.example.project_stay.model.Amenity

object AmenityRepository {
    val amenities = listOf(
        Amenity(1, "Pool", R.drawable.baseline_pool_24),
        Amenity(2, "Gym", R.drawable.baseline_fitness_center_24),
        Amenity(3, "WiFi", R.drawable.baseline_wifi_24),
        Amenity(4, "Parking", R.drawable.baseline_local_parking_24),
        Amenity(5, "AC", R.drawable.baseline_air_24),
        Amenity(6, "TV", R.drawable.baseline_tv_24),
        Amenity(7, "Cafe", R.drawable.baseline_restaurant_24),
        Amenity(8, "Bar", R.drawable.baseline_wine_bar_24),
        Amenity(9, "Laundry", R.drawable.baseline_local_laundry_service_24),
        Amenity(10, "Spa", R.drawable.baseline_spa_24),
        Amenity(11, "Pets", R.drawable.baseline_pets_24),
        Amenity(12, "Library", R.drawable.baseline_local_library_24)
    )
}