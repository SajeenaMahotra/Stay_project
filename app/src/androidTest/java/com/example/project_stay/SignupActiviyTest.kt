package com.example.project_stay

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.project_stay.ui.activity.SignupActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SignupActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(SignupActivity::class.java)

    @Test
    fun testSignupUI() {
        // Enter email in the EmailView EditText
        onView(withId(R.id.emailInput)).perform(
            typeText("anjal@gmail.com")
        )

        // Enter password in the passwordView EditText
        onView(withId(R.id.passwordInput)).perform(
            typeText("anjal123")
        )

        // Enter confirm password in the confirmPasswordView EditText
        onView(withId(R.id.confirmPasswordInput)).perform(
            typeText("anjal123")
        )

        // Close the soft keyboard
        closeSoftKeyboard()

        // Simulate a delay to observe the UI (optional)
        Thread.sleep(1000)

        // Click the signup button
        onView(withId(R.id.signupButton)).perform(
            click()
        )

        // Simulate a delay to observe the UI (optional)
        Thread.sleep(1000)

        // Verify the "Already have an account? Login" text is displayed correctly
        onView(withId(R.id.alreadyText)).check(matches(withText("Already have an account?")))

        // Verify the "Sign Up" button text
        onView(withId(R.id.signupButton)).check(matches(withText("Sign Up")))

        // Verify the "Confirm Password" hint text
        onView(withId(R.id.confirmPasswordInput)).check(matches(withHint("Confirm Password")))
    }
}