package com.zharkovsky.memes.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zharkovsky.memes.utils.Constants
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var nextActivityIntent = Intent(this, LoginActivity::class.java)
        startActivityAfterDelay(nextActivityIntent, Constants.SPLASH_DELAY)
    }

    private fun startActivityAfterDelay(activity: Intent, delay: Long) {
        Timer().schedule(timerTask {
            startActivity(activity)
            finish()
        }, delay)
    }
}