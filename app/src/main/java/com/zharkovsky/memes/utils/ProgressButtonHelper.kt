package com.zharkovsky.memes.utils

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.zharkovsky.memes.R

class ProgressButtonHelper internal constructor(view: View, btnText: String?) {
    private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    private val textView: TextView = view.findViewById(R.id.progressBtnText)

    private var text: String? = btnText

    init {
        textView.text = text
    }

    fun buttonActivated() {
        progressBar.visibility = View.VISIBLE
        textView.text = ""
    }

    fun buttonFinished() {
        progressBar.visibility = View.GONE
        textView.text = text
    }
}