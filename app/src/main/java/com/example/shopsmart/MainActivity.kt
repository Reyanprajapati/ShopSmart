package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {

            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isNotEmpty() && pass.isNotEmpty()) {

                Toast.makeText(
                    this,
                    "Login Successful 🎉",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, HomeActivity::class.java)

                intent.putExtra("USERNAME", user)

                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}