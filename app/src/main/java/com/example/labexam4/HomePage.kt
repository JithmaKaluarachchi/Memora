package com.example.labexam4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val splashDelayMillis: Long = 3000 //  3seconds

        // Create a thread to delay the redirection
        Thread {
            try {
                // Sleep for the splash delay duration
                Thread.sleep(splashDelayMillis)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                // Redirect to MainActivity after the delay
                startActivity(Intent(this@HomePage, MainActivity::class.java))
                finish() // Finish the splash activity to prevent going back to it
            }
        }.start()
    }
}