package com.zharkovsky.memes.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.zharkovsky.memes.R
import com.zharkovsky.memes.utils.Constants
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = createLayout()
        setLogo(layout, R.drawable.ic_surf_logo)
        setContentView(layout)

        var nextActivityIntent = Intent(this, LoginActivity::class.java)
        startActivityAfterDelay(nextActivityIntent, Constants.SPLASH_DELAY)
    }

    private fun createLayout(): RelativeLayout {
        val layout = RelativeLayout(this)
        layout.setBackgroundResource(R.color.background)
        layout.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )
        return layout
    }

    private fun setLogo(layout: RelativeLayout, resId: Int) {
        val logo = ImageView(this)
        val logoParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        logoParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        logo.setImageResource(resId)
        layout.addView(logo, logoParams)
    }

    private fun startActivityAfterDelay(activity: Intent, delay: Long) {
        Timer().schedule(timerTask {
            startActivity(activity)
            finish()
        }, delay)
    }
}
