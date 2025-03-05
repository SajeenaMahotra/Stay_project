package com.example.project_stay.model

import android.os.Parcel
import android.os.Parcelable

data class BookingModel(
    val bookingId: String = "",
    val hotelId: String = "",
    val roomId: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val userId: String = "",
    val status: String = "active", // Can be "active" or "completed"
    var hotelName: String = "",// Add this field
    var userName: String = "" // Add this field
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "active"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookingId)
        parcel.writeString(hotelId)
        parcel.writeString(roomId)
        parcel.writeString(checkInDate)
        parcel.writeString(checkOutDate)
        parcel.writeString(userId)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingModel> {
        override fun createFromParcel(parcel: Parcel): BookingModel {
            return BookingModel(parcel)
        }

        override fun newArray(size: Int): Array<BookingModel?> {
            return arrayOfNulls(size)
        }
    }
}