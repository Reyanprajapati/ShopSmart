package com.example.shopsmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ProfileActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var tvProfileUsername: TextView
    private lateinit var tvProfileUserType: TextView
    private lateinit var btnLogout: Button

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        initPrefs()
        loadUserData()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvProfileUsername = findViewById(R.id.tvProfileUsername)
        tvProfileUserType = findViewById(R.id.tvProfileUserType)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun initPrefs() {
        prefs = getSharedPreferences("ShopSmartPrefs", Context.MODE_PRIVATE)
    }

    private fun loadUserData() {
        val username = prefs.getString("USERNAME", "User") ?: "User"
        val userType = prefs.getString("USER_TYPE", "Customer") ?: "Customer"

        tvProfileUsername.text = username
        tvProfileUserType.text = "$userType Account"
    }

    private fun setupListeners() {
        // 1. Back Arrow
        btnBack.setOnClickListener {
            finish()
        }

        // 2. Logout Button
        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}