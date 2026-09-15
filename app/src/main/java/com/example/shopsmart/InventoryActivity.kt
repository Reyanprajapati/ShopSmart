package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class InventoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inventory)

        val btnBack = findViewById<TextView>(R.id.btnBack)
        val btnAddProduct = findViewById<TextView>(R.id.btnAddProduct)

        val navHome = findViewById<TextView>(R.id.btnNavHome)
        val navInventory = findViewById<TextView>(R.id.btnNavInventory)
        val navProfile = findViewById<TextView>(R.id.btnNavProfile)

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Add Product button
        btnAddProduct.setOnClickListener {
            Toast.makeText(
                this,
                "Add Product feature coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Home
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Inventory - already here
        navInventory.setOnClickListener {
            // Already on Inventory
        }

        // Profile
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}