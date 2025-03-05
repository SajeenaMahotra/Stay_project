package com.example.project_stay

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingValidationTest {

    private fun isBookingValid(userId: String?, roomId: String?, checkIn: String?, checkOut: String?): Boolean {
        if (userId.isNullOrEmpty() || roomId.isNullOrEmpty()) return false
        if (checkIn.isNullOrEmpty() || checkOut.isNullOrEmpty()) return false
        return checkIn < checkOut // Check-in must be before check-out
    }

    @Test
    fun validBooking_returnsTrue() {
        assertTrue(isBookingValid("user123", "roomA1", "2025-03-10", "2025-03-15"))
        assertTrue(isBookingValid("guest456", "roomB2", "2025-04-01", "2025-04-05"))
    }

    @Test
    fun invalidBooking_returnsFalse() {
        assertFalse(isBookingValid("", "roomA1", "2025-03-10", "2025-03-15")) // No user ID
        assertFalse(isBookingValid("user123", "", "2025-03-10", "2025-03-15")) // No room ID
        assertFalse(isBookingValid("user123", "roomA1", "2025-03-15", "2025-03-10")) // Invalid date order
        assertFalse(isBookingValid("user123", "roomA1", "", "2025-03-15")) // Missing check-in date
    }
}
