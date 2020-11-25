package com.zharkovsky.memes.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zharkovsky.memes.MainActivity
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var nextActivityIntent = Intent(this, MainActivity::class.java)
        startActivityAfterDelay(nextActivityIntent, 300)
    }
    private fun startActivityAfterDelay(activity: Intent, delay: Long) {
        Timer().schedule(timerTask {
            startActivity(activity)
            finish()
        }, delay)
    }
}