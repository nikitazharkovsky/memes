package com.zharkovsky.memes.activities

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.zharkovsky.memes.R
import com.zharkovsky.memes.utils.Constants
import com.zharkovsky.memes.utils.ProgressButtonHelper
import studio.carbonylgroup.textfieldboxes.ExtendedEditText
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes
import java.util.*
import kotlin.concurrent.timerTask


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var loginTextFieldBoxes = findViewById<TextFieldBoxes>(R.id.text_field_boxes_login)
        var loginExtendedEditText = findViewById<ExtendedEditText>(R.id.extended_edit_text_login)

        var passwordTextFieldBoxes = findViewById<TextFieldBoxes>(R.id.text_field_boxes_password)
        var passwordExtendedEditText = findViewById<ExtendedEditText>(R.id.extended_edit_text_password)

        var progressButton =  findViewById<View>(R.id.enter_button)
        var progressButtonHelper = ProgressButtonHelper(progressButton, "ВОЙТИ")

        loginTextFieldBoxes.setSimpleTextChangeWatcher { s: String, b: Boolean -> onTextChanged(loginTextFieldBoxes, s, b) }

        passwordTextFieldBoxes.setSimpleTextChangeWatcher { s: String, b: Boolean -> onTextChanged(passwordTextFieldBoxes, s, b) }
        passwordExtendedEditText.setOnFocusChangeListener { _, b -> onFocusChanged(passwordTextFieldBoxes, b) }

        passwordTextFieldBoxes.getEndIconImageButton().setOnClickListener { onShowHidePasswordClick(passwordTextFieldBoxes, passwordExtendedEditText) }

        progressButton.setOnClickListener {
            val inputIsValid = validateInput(loginTextFieldBoxes, loginExtendedEditText, true) and
                    validateInput(passwordTextFieldBoxes, passwordExtendedEditText, !loginTextFieldBoxes.isOnError)
            if (inputIsValid) {
                onLoginClick(progressButtonHelper)
            }
        }
    }

    override fun onBackPressed() {
        //не переходим на сплэш
    }

    private fun onTextChanged(textFieldBoxes: TextFieldBoxes, theNewText: String, isError: Boolean) {
        if (isError and (theNewText.length > 0)) {
            textFieldBoxes.removeError()
        }
    }

    private fun onFocusChanged(
        textBox: TextFieldBoxes,
        focused: Boolean
    ) {
        if (focused) {
            textBox.helperText = getString(R.string.password_helper)
        } else {
            textBox.helperText = " "
        }
    }

    private fun onShowHidePasswordClick(
            textFieldBoxes: TextFieldBoxes,
            extendedEditText: ExtendedEditText) {
        if (textFieldBoxes.endIconResourceId == R.drawable.ic_show) {
            textFieldBoxes.setEndIcon(R.drawable.ic_hide)
            extendedEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            textFieldBoxes.setEndIcon(R.drawable.ic_show)
            extendedEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun validateInput(
            textFieldBoxes: TextFieldBoxes,
            extendedEditText: ExtendedEditText,
            giveFocus: Boolean): Boolean {
        if (extendedEditText.text.isEmpty()) {
            textFieldBoxes.setError(getString(R.string.empty_field), giveFocus)
            return false
        }
        return true
    }

    private fun onLoginClick(progressButtonHelper: ProgressButtonHelper) {
        progressButtonHelper.buttonActivated()

        Timer().schedule(timerTask {
            runOnUiThread(progressButtonHelper::buttonFinished)
        }, Constants.PROGRESS_BUTTON_DELAY)
    }

}