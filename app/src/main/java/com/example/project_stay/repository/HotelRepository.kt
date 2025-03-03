    package com.example.project_stay.repository

    import android.content.Context
    import android.net.Uri
    import androidx.lifecycle.LiveData
    import com.example.project_stay.model.Amenity
    import com.example.project_stay.model.Hotel
    import com.example.project_stay.model.RoomModel
    import com.example.project_stay.model.UserModel
    import com.google.firebase.auth.FirebaseUser

    interface HotelRepository {
        fun login(email:String, password:String,callback:(Boolean,String, String?)->Unit)

        fun forgetPassword(email:String,callback: (Boolean,String)->Unit)

        fun getCurrentHotel(): FirebaseUser?

        fun getHotelDetails(hotelId: String, callback: (Hotel?) -> Unit)

        fun saveHotelDetails(hotel: Hotel, callback: (Boolean) -> Unit)

        fun addRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit)

        fun getRooms(hotelId: String, callback: (List<RoomModel>) -> Unit)

        fun updateRoom(hotelId: String, room: RoomModel, callback: (Boolean, String) -> Unit)

        fun deleteRoom(hotelId: String, roomId: String, callback: (Boolean, String) -> Unit)

        fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

        fun getFileNameFromUri(context: Context, uri: Uri): String?

        fun getHotelImageUrl(hotelId: String, callback: (String?) -> Unit)

        fun getHotels(): LiveData<List<Hotel>>

        fun getHotelDetails(hotelId: String): LiveData<Hotel?>

        fun addToWishList(userId: String,hotelId: String, isWishlisted: Boolean, callback: (Boolean, String) -> Unit)

        fun getWishlistedHotels(userId: String, callback: (List<Hotel>?, Boolean, String) -> Unit)



    }