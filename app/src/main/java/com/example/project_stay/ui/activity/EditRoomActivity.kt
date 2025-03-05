package com.example.project_stay.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_stay.R
import com.example.project_stay.databinding.ActivityEditRoomBinding
import com.example.project_stay.model.Hotel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.viewmodel.HotelViewModel

class EditRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditRoomBinding
    lateinit var viewModel: HotelViewModel
    private lateinit var roomId: String
    private lateinit var hotelId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var repo = HotelRepositoryImpl()
        viewModel = HotelViewModel(repo)

        val roomNameEditText = binding.roomNameEditText
        val numberOfRoomsEditText = binding.numberOfRoomsEditText
        val numberOfGuestsEditText = binding.numberOfGuestsEditText
        val pricePerNightEditText = binding.pricePerNightEditText
        val saveButton = binding.saveButton
        val deleteButton = binding.deleteButton

        hotelId = intent.getStringExtra("HOTEL_ID") ?: ""
        Log.d("received hotel details", "Hotel ID: ${hotelId}")
        roomId = intent.getStringExtra("ROOM_ID") ?: ""
        val roomName = intent.getStringExtra("ROOM_NAME")
        Log.d("room details", "room name: ${roomName}")
        val numberOfRooms = intent.getIntExtra("NUMBER_OF_ROOMS", 0)
        val numberOfGuests = intent.getIntExtra("NUMBER_OF_GUESTS", 0)
        val pricePerNight = intent.getDoubleExtra("PRICE_PER_NIGHT", 0.0)

        roomNameEditText.setText(roomName)
        numberOfRoomsEditText.setText(numberOfRooms.toString())
        numberOfGuestsEditText.setText(numberOfGuests.toString())
        pricePerNightEditText.setText(pricePerNight.toString())

        binding.backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            val updatedRoom = RoomModel(
                roomId = roomId,
                roomName = roomNameEditText.text.toString(),
                hotelId = hotelId,
                numberOfRooms = numberOfRoomsEditText.text.toString().toInt(),
                numberOfGuests = numberOfGuestsEditText.text.toString().toInt(),
                pricePerNight = pricePerNightEditText.text.toString().toDouble()
            )
            Log.d("UpdateRoomDebug", "Updating room with ID: ${roomId}")
            Log.d("UpdateRoomDebug", "Updating room with ID: ${hotelId}")

            viewModel.updateRoom(hotelId, updatedRoom)
        }

        deleteButton.setOnClickListener {
            viewModel.deleteRoom(hotelId, roomId)
        }

        viewModel.roomUpdateStatus.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Room updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Navigate back to the previous activity
            } else {
                Toast.makeText(this, "Failed to update room", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.roomDeleteStatus.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Room deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Navigate back to the previous activity
            } else {
                Toast.makeText(this, "Failed to delete room", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
