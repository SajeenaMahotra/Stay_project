package com.example.project_stay.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_stay.R
import com.example.project_stay.adapter.AmenityAdapter
import com.example.project_stay.databinding.ActivityHotelInfoBinding
import com.example.project_stay.databinding.HotelCardBinding
import com.example.project_stay.model.BookingModel
import com.example.project_stay.model.RoomModel
import com.example.project_stay.repository.HotelRepositoryImpl
import com.example.project_stay.ui.adapter.RoomListAdapter
import com.example.project_stay.viewmodel.HotelViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class HotelInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelInfoBinding
    private lateinit var checkInDate: EditText
    private lateinit var checkOutDate: EditText
    private lateinit var bookButton: Button
    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomListAdapter: RoomListAdapter
    private lateinit var selectedRoom: RoomModel
    lateinit var hotelViewModel: HotelViewModel
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var hotelId: String = ""
    private var rooms: List<RoomModel> = emptyList() // Declare rooms as a class-level variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = HotelRepositoryImpl()
        hotelViewModel = HotelViewModel(repo)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Get hotelId from intent
        hotelId = intent.getStringExtra("hotelId") ?: return

        val databaseHotel = FirebaseDatabase.getInstance().getReference("hotels")
        val lol = SelectAmenitiesActivity()
        // Fetch selected amenities
        lol.getSelectedAmenities(hotelId, databaseHotel) { selectedAmenities ->
            val adapter = AmenityAdapter(selectedAmenities, hotelId, databaseHotel, false) { amenity ->
                // Handle amenity click if necessary
            }

            // Set the adapter to the RecyclerView
            binding.viewAmenities.layoutManager = GridLayoutManager(this, 4)
            binding.viewAmenities.adapter = adapter
        }

        // Initialize views
        checkInDate = findViewById(R.id.checkInDate)
        checkOutDate = findViewById(R.id.checkOutDate)
        bookButton = findViewById(R.id.bookButton)
        roomRecyclerView = findViewById(R.id.roomRecyclerView)

        binding.checkInDate.isClickable = true
        binding.checkInDate.isFocusable = false

        binding.checkOutDate.isClickable = true
        binding.checkOutDate.isFocusable = false

        // Set up RecyclerView
        roomRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch hotel details and rooms
        fetchHotelDetails(hotelId)
        fetchRooms(hotelId)

        // Set up date pickers
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        checkInDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                checkInDate.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)
            datePickerDialog.show()
        }

        checkOutDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                checkOutDate.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)
            datePickerDialog.show()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        // Set up booking button
        bookButton.setOnClickListener {
            val checkIn = checkInDate.text.toString()
            val checkOut = checkOutDate.text.toString()

            if (checkIn.isEmpty() || checkOut.isEmpty()) {
                Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!this::selectedRoom.isInitialized) {
                Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkRoomAvailability(selectedRoom.roomId, checkIn, checkOut)
        }
    }

    private fun fetchHotelDetails(hotelId: String) {
        val hotelCardBinding = HotelCardBinding.inflate(layoutInflater)
        binding.mainContainer.addView(hotelCardBinding.root)

        hotelViewModel.getHotelDetails(hotelId).observe(this@HotelInfoActivity) { hotel ->
            hotel?.let {
                Picasso.get().load(it.imageUrl).into(hotelCardBinding.hotelImage)
                hotelCardBinding.hotelName.text = it.name
                hotelCardBinding.location.text = it.location
                hotelCardBinding.price.text = "£${hotel.lowestPrice}"
                hotelCardBinding.perNightText.text = "per night"
                binding.hotelDescription.text = it.description
            }
        }
    }

    private fun fetchRooms(hotelId: String) {
        database.child("rooms").child(hotelId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomList = mutableListOf<RoomModel>()
                for (roomSnapshot in snapshot.children) {
                    val room = roomSnapshot.getValue(RoomModel::class.java)
                    room?.let { roomList.add(it) }
                }
                rooms = roomList // Update the class-level rooms variable

                // Initialize adapter with click listener
                roomListAdapter = RoomListAdapter(rooms) { room ->
                    selectedRoom = room // Set the selected room
                    Toast.makeText(this@HotelInfoActivity, "Selected: ${room.roomName}", Toast.LENGTH_SHORT).show()
                }
                roomRecyclerView.adapter = roomListAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HotelInfoActivity, "Failed to fetch rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkRoomAvailability(roomId: String, checkIn: String, checkOut: String) {
        database.child("bookings").orderByChild("roomId").equalTo(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var activeBookings = 0
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(BookingModel::class.java)
                    if (booking != null && booking.status == "active" && isDateOverlap(checkIn, checkOut, booking.checkInDate, booking.checkOutDate)) {
                        activeBookings++
                    }
                }

                // Check if the number of active bookings is less than the number of rooms available
                val selectedRoom = rooms.find { it.roomId == roomId }
                if (selectedRoom != null && activeBookings < selectedRoom.numberOfRooms) {
                    val intent = Intent(this@HotelInfoActivity, BookingConfirmationActivity::class.java)
                    intent.putExtra("hotelId", hotelId)
                    intent.putExtra("roomId", roomId)
                    intent.putExtra("checkIn", checkIn)
                    intent.putExtra("checkOut", checkOut)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@HotelInfoActivity, "Room is not available for the selected dates", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HotelInfoActivity, "Failed to check room availability", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isDateOverlap(checkIn: String, checkOut: String, existingCheckIn: String, existingCheckOut: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val checkInDate = dateFormat.parse(checkIn)
        val checkOutDate = dateFormat.parse(checkOut)
        val existingCheckInDate = dateFormat.parse(existingCheckIn)
        val existingCheckOutDate = dateFormat.parse(existingCheckOut)

        return (checkInDate.before(existingCheckOutDate) && checkOutDate.after(existingCheckInDate))
    }
}