package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class SignupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        val name =
            findViewById<EditText>(R.id.etSignupName)

        val username =
            findViewById<EditText>(R.id.etSignupUsername)

        val mobile =
            findViewById<EditText>(R.id.etSignupMobile)

        val password =
            findViewById<EditText>(R.id.etSignupPassword)

        val confirmPassword =
            findViewById<EditText>(R.id.etConfirmPassword)

        val userTypeGroup =
            findViewById<RadioGroup>(
                R.id.signupUserTypeGroup
            )

        val signupButton =
            findViewById<Button>(
                R.id.btnCreateAccount
            )

        val loginButton =
            findViewById<TextView>(
                R.id.btnGoLogin
            )


        signupButton.setOnClickListener {

            val fullName =
                name.text.toString().trim()

            val user =
                username.text.toString().trim()

            val mobileNumber =
                mobile.text.toString().trim()

            val pass =
                password.text.toString().trim()

            val confirm =
                confirmPassword.text.toString().trim()


            // CHECK EMPTY

            if (
                fullName.isEmpty() ||
                user.isEmpty() ||
                mobileNumber.isEmpty() ||
                pass.isEmpty() ||
                confirm.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // CHECK MOBILE

            if (mobileNumber.length != 10) {

                Toast.makeText(
                    this,
                    "Enter valid 10 digit mobile number",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // CHECK PASSWORD

            if (pass != confirm) {

                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // CHECK USER TYPE

            val selectedId =
                userTypeGroup.checkedRadioButtonId

            if (selectedId == -1) {

                Toast.makeText(
                    this,
                    "Please select Customer or Seller",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val selectedRadio =
                findViewById<RadioButton>(
                    selectedId
                )

            val userType =
                selectedRadio.text.toString()


            // SAVE ALL DETAILS

            val preferences =
                getSharedPreferences(
                    "ShopSmartPrefs",
                    MODE_PRIVATE
                )

            preferences.edit()

                .putString(
                    "USERNAME",
                    user
                )

                .putString(
                    "NAME",
                    fullName
                )

                .putString(
                    "MOBILE",
                    mobileNumber
                )

                .putString(
                    "PASSWORD",
                    pass
                )

                .putString(
                    "USER_TYPE",
                    userType
                )

                .apply()


            Toast.makeText(
                this,
                "Account Created Successfully 🎉",
                Toast.LENGTH_SHORT
            ).show()


            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
        }


        loginButton.setOnClickListener {

            finish()
        }
    }
}