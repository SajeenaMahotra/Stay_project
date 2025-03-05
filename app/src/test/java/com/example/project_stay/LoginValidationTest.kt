package com.example.project_stay

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginValidationTest {
    fun isValidUsername(username: String?): Boolean {
        if (username.isNullOrEmpty()) return false
        return username.length in 3..20 && username.all { it.isLetterOrDigit() }
    }

    @Test
    fun validEmail_returnsTrue() {
        assertTrue(isValidEmail("batsal@gmail.com"))
    }

    @Test
    fun invalidEmail_returnsFalse() {
        assertFalse(isValidEmail("invalid-email"))
    }

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrEmpty()) return false

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    @Test
    fun validUsername_returnsTrue() {
        assertTrue(isValidUsername("Anjal")) // Valid username
        assertTrue(isValidUsername("Anjal2024")) // Another valid one
    }

    @Test
    fun invalidUsername_returnsFalse() {
        assertFalse(isValidUsername("JD")) // Less than 3 characters
        assertFalse(isValidUsername("ThisIsAVeryLongUsername123")) // More than 20 characters
        assertFalse(isValidUsername("user name")) // Contains spaces
        assertFalse(isValidUsername("user@123")) // Contains special characters
        assertFalse(isValidUsername("")) // Empty username
    }
}


