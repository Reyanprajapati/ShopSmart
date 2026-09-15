package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        val username = intent.getStringExtra("USERNAME") ?: "User"

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val inventoryButton = findViewById<Button>(R.id.btnInventory)

        val navHome = findViewById<TextView>(R.id.btnNavHome)
        val navInventory = findViewById<TextView>(R.id.btnNavInventory)
        val navProfile = findViewById<TextView>(R.id.btnNavProfile)

        welcomeText.text = "Welcome, $username 👋"

        // Full Inventory
        inventoryButton.setOnClickListener {
            startActivity(
                Intent(this, InventoryActivity::class.java)
            )
        }

        // Home
        navHome.setOnClickListener {
            // Already on Home
        }

        // Inventory
        navInventory.setOnClickListener {
            startActivity(
                Intent(this, InventoryActivity::class.java)
            )
        }

        // Profile
        navProfile.setOnClickListener {
            startActivity(
                Intent(this, ProfileActivity::class.java)
            )
        }
    }
}