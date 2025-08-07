package com.elts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen
        val splashScreen = installSplashScreen()

        // Keep splash visible for extra time (e.g., 2 seconds)
        splashScreen.setKeepOnScreenCondition {
            // We'll use a flag to delay screen for 2 seconds
            !isReadyToProceed
        }

        super.onCreate(savedInstanceState)

        // Delay using a handler
        android.os.Handler(mainLooper).postDelayed({
            isReadyToProceed = true

            // After delay, go to correct screen
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                startActivity(Intent(this, HomeSCreenActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 5000) // 2000 milliseconds = 2 seconds
    }

    companion object {
        private var isReadyToProceed = false
    }
}
