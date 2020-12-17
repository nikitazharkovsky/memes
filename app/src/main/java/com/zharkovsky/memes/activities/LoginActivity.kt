package com.zharkovsky.memes.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zharkovsky.memes.R
import com.zharkovsky.memes.services.NetworkService
import com.zharkovsky.memes.utils.ProgressButtonHelper
import com.zharkovsky.memes.models.LoginUserRequestDto
import com.zharkovsky.memes.models.AuthInfoDto
import com.zharkovsky.memes.utils.Constants
import com.zharkovsky.memes.utils.Constants.APP_REFERENCES
import com.zharkovsky.memes.utils.Constants.FIRST_NAME_FIELD
import com.zharkovsky.memes.utils.Constants.ID_FIELD
import com.zharkovsky.memes.utils.Constants.LAST_NAME_FIELD
import com.zharkovsky.memes.utils.Constants.TOKEN_FIELD
import com.zharkovsky.memes.utils.Constants.USERNAME_FIELD
import com.zharkovsky.memes.utils.Constants.USER_DESCRIPTION_FIELD
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import studio.carbonylgroup.textfieldboxes.ExtendedEditText
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes
import java.util.*
import kotlin.concurrent.timerTask

class LoginActivity : AppCompatActivity() {
    private lateinit var authLayout: LinearLayout
    private lateinit var loginTextFieldBoxes: TextFieldBoxes
    private lateinit var loginExtendedEditText: ExtendedEditText
    private lateinit var passwordTextFieldBoxes: TextFieldBoxes
    private lateinit var passwordExtendedEditText: ExtendedEditText
    private lateinit var progressButton: View
    private lateinit var progressButtonHelper: ProgressButtonHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        initTextChangeWatchers()
        initFocusChangeListeners()
        initClickListeners()
    }

    private fun initViews() {
        authLayout = findViewById(R.id.auth_layout)
        loginTextFieldBoxes = findViewById(R.id.text_field_boxes_login)
        loginExtendedEditText = findViewById(R.id.extended_edit_text_login)
        passwordTextFieldBoxes = findViewById(R.id.text_field_boxes_password)
        passwordExtendedEditText = findViewById(R.id.extended_edit_text_password)
        progressButton = findViewById<View>(R.id.enter_button)
        progressButtonHelper = ProgressButtonHelper(progressButton, "ВОЙТИ")
    }

    private fun initTextChangeWatchers() {
        loginTextFieldBoxes.setSimpleTextChangeWatcher { s, b ->
            onTextChanged(loginTextFieldBoxes, s, b)
        }
        passwordTextFieldBoxes.setSimpleTextChangeWatcher { s, b ->
            onTextChanged(passwordTextFieldBoxes, s, b)
        }
    }

    private fun initFocusChangeListeners() {
        passwordExtendedEditText.setOnFocusChangeListener { _, b ->
            onFocusChanged(passwordTextFieldBoxes, b)
        }
    }

    private fun initClickListeners() {
        passwordTextFieldBoxes.endIconImageButton.setOnClickListener {
            onShowHidePasswordClick(passwordTextFieldBoxes, passwordExtendedEditText)
        }

        progressButton.setOnClickListener {
            val inputIsValid = validateInputs()
            if (inputIsValid) {
                val request = LoginUserRequestDto(
                        loginExtendedEditText.text.toString(),
                        passwordExtendedEditText.text.toString()
                )
                onLoginClick(authLayout, progressButtonHelper, request)
            }
        }
    }

    private fun onTextChanged(
            textFieldBoxes: TextFieldBoxes,
            theNewText: String,
            isError: Boolean
    ) {
        if (isError and (theNewText.isNotEmpty())) {
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
            extendedEditText: ExtendedEditText
    ) {
        if (textFieldBoxes.endIconResourceId == R.drawable.ic_show) {
            textFieldBoxes.setEndIcon(R.drawable.ic_hide)
            extendedEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            textFieldBoxes.setEndIcon(R.drawable.ic_show)
            extendedEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun validateInputs(): Boolean {
        return validateInput(
                loginTextFieldBoxes,
                loginExtendedEditText,
                true
        ) and validateInput(
                passwordTextFieldBoxes,
                passwordExtendedEditText,
                !loginTextFieldBoxes.isOnError
        )
    }

    private fun validateInput(
            textFieldBoxes: TextFieldBoxes,
            extendedEditText: ExtendedEditText,
            giveFocus: Boolean
    ): Boolean {
        if (extendedEditText.text.isEmpty()) {
            textFieldBoxes.setError(getString(R.string.empty_field), giveFocus)
            return false
        }
        return true
    }

    private fun onLoginClick(
            root: View,
            progressButtonHelper: ProgressButtonHelper,
            request: LoginUserRequestDto
    ) {
        val rootActivity = this
        progressButtonHelper.buttonActivated()

        Timer().schedule(timerTask {
            NetworkService.getInstance()
                    .jsonApi
                    .login(request)
                    .enqueue(rootActivity.callback(progressButtonHelper, root))
        }, Constants.PROGRESS_BUTTON_DELAY)
    }

    private fun LoginActivity.callback(
            progressButtonHelper: ProgressButtonHelper,
            root: View
    ): Callback<AuthInfoDto> {
        return object : Callback<AuthInfoDto> {
            override fun onResponse(call: Call<AuthInfoDto>, response: Response<AuthInfoDto>) {
                if (response.code() != 200) {
                    showSnackBar()
                } else {
                    this@LoginActivity.saveResponseToSharedPref(this@callback, response.body()!!)
                    val nextActivityIntent = Intent(this@callback, MainActivity::class.java)
                    this@LoginActivity.startActivity(nextActivityIntent)
                    this@LoginActivity.finish()
                }
                this@LoginActivity.runOnUiThread(progressButtonHelper::buttonFinished)
            }

            override fun onFailure(call: Call<AuthInfoDto>, t: Throwable) {
                showSnackBar()
                this@LoginActivity.runOnUiThread(progressButtonHelper::buttonFinished)
            }

            private fun showSnackBar() {
                val snackBar = Snackbar.make(
                        root,
                        this@LoginActivity.getString(R.string.auth_error),
                        Snackbar.LENGTH_LONG
                )
                snackBar.view.setBackgroundResource(R.color.errorBackground)
                snackBar.show()
            }
        }
    }

    private fun saveResponseToSharedPref(activity: Activity, response: AuthInfoDto) {
        val sharedPref = activity.getSharedPreferences(APP_REFERENCES, Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            putString(TOKEN_FIELD, response.accessToken)
            putInt(ID_FIELD, response.userInfo!!.id)
            putString(USERNAME_FIELD, response.userInfo!!.username)
            putString(FIRST_NAME_FIELD, response.userInfo!!.firstName)
            putString(LAST_NAME_FIELD, response.userInfo!!.lastName)
            putString(USER_DESCRIPTION_FIELD, response.userInfo!!.userDescription)
            commit()
        }
    }
}
