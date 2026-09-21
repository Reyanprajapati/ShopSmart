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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val userTypeGroup = findViewById<RadioGroup>(R.id.userTypeGroup)
        val signupButton = findViewById<TextView>(R.id.btnSignup)
        loginButton.setOnClickListener {
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()
            // CHECK EMPTY FIELDS
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // CHECK USER TYPE
            val selectedId = userTypeGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(
                    this,
                    "Please select Seller or Customer",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val selectedRadio =
                findViewById<RadioButton>(selectedId)
            val selectedUserType =
                selectedRadio.text.toString().trim()
            // GET SAVED ACCOUNT DETAILS
            val preferences =
                getSharedPreferences(
                    "ShopSmartPrefs",
                    MODE_PRIVATE
                )
            val savedUsername =
                preferences.getString("USERNAME", "")
            val savedPassword =
                preferences.getString("PASSWORD", "")
            val savedUserType =
                preferences.getString("USER_TYPE", "")
            // CHECK IF ACCOUNT EXISTS
            if (savedUsername.isNullOrEmpty() ||
                savedPassword.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this,
                    "No account found. Please Sign Up first.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // CHECK USERNAME
            if (user != savedUsername) {
                Toast.makeText(
                    this,
                    "Invalid username",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // CHECK PASSWORD
            if (pass != savedPassword) {
                Toast.makeText(
                    this,
                    "Invalid password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // CHECK USER TYPE
            if (!savedUserType.isNullOrEmpty() &&
                !selectedUserType.equals(
                    savedUserType,
                    ignoreCase = true
                )
            ) {
                Toast.makeText(
                    this,
                    "Incorrect account type",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // SAVE CURRENT LOGIN
            preferences.edit()
                .putString("USERNAME", savedUsername)
                .putString("USER_TYPE", savedUserType)
                .apply()
            Toast.makeText(
                this,
                "Login Successful 🎉",
                Toast.LENGTH_SHORT
            ).show()
            // OPEN SELLER OR CUSTOMER HOME
            if (selectedUserType.equals(
                    "Seller",
                    ignoreCase = true
                )
            ) {
                val intent =
                    Intent(
                        this,
                        HomeActivity::class.java
                    )
                intent.putExtra(
                    "USERNAME",
                    savedUsername
                )
                startActivity(intent)
            } else {
                val intent =
                    Intent(
                        this,
                        CustomerHomeActivity::class.java
                    )
                intent.putExtra(
                    "USERNAME",
                    savedUsername
                )
                startActivity(intent)
            }
            finish()
        }
        // SIGNUP
        signupButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignupActivity::class.java
                )
            )
        }
    }
}