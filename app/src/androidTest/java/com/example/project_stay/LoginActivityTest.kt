package com.example.project_stay

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.project_stay.ui.activity.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginUI() {
        // Enter email in the EmailView EditText
        onView(withId(R.id.emailInput)).perform(
            typeText("anjal@gmail.com")
        )

        // Enter password in the passwordView EditText
        onView(withId(R.id.passwordInput)).perform(
            typeText("anjal123")
        )

        // Close the soft keyboard
        closeSoftKeyboard()

        // Simulate a delay to observe the UI (optional)
        Thread.sleep(1000)

        // Click the login button
        onView(withId(R.id.buttonLogin)).perform(
            click()
        )

        // Simulate a delay to observe the UI (optional)
        Thread.sleep(1000)

        // Verify the app name text is displayed correctly
        //onView(withId(R.id.stayLogo)).check(matches(withText("Stay")))

        // Verify the "Don't have an account? Sign Up" button text
        onView(withId(R.id.textCreateOne)).check(matches(withText("Create One.")))

        // Verify the "Forget Password?" button text
        onView(withId(R.id.ForgotPassword)).check(matches(withText("Forgot Password?")))
    }
}