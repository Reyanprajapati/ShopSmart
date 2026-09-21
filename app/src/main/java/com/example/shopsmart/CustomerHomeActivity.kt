package com.example.shopsmart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class CustomerHomeActivity : ComponentActivity() {

    private lateinit var tvWelcomeUser: TextView
    private lateinit var btnNotifications: Button
    private lateinit var btnCart: Button
    private lateinit var btnProfile: Button
    private lateinit var etHomeSearch: EditText
    private lateinit var btnBrowseAll: Button
    private lateinit var cardCategoryRice: LinearLayout
    private lateinit var cardCategoryDairy: LinearLayout
    private lateinit var cardCategorySnacks: LinearLayout
    private lateinit var cardCategoryDrinks: LinearLayout
    private lateinit var cardMyOrders: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_home)

        initViews()
        loadUserData()
        setupListeners()
    }

    private fun initViews() {
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnCart = findViewById(R.id.btnCart)
        btnProfile = findViewById(R.id.btnProfile)
        etHomeSearch = findViewById(R.id.etHomeSearch)
        btnBrowseAll = findViewById(R.id.btnBrowseAll)
        cardCategoryRice = findViewById(R.id.cardCategoryRice)
        cardCategoryDairy = findViewById(R.id.cardCategoryDairy)
        cardCategorySnacks = findViewById(R.id.cardCategorySnacks)
        cardCategoryDrinks = findViewById(R.id.cardCategoryDrinks)
        cardMyOrders = findViewById(R.id.cardMyOrders)
    }

    private fun loadUserData() {
        val prefs = getSharedPreferences("ShopSmartPrefs", Context.MODE_PRIVATE)
        val username = prefs.getString("USERNAME", "Customer") ?: "Customer"
        tvWelcomeUser.text = "Hello, $username 👋"
    }

    private fun setupListeners() {
        // Search bar enter listener
        etHomeSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                openProducts("", etHomeSearch.text.toString().trim())
                true
            } else {
                false
            }
        }

        // Top action buttons
        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Browse All
        btnBrowseAll.setOnClickListener {
            openProducts("All", "")
        }

        // Orders
        cardMyOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        // Direct Category Click Listeners
        cardCategoryRice.setOnClickListener { openProducts("Grocery", "") }
        cardCategoryDairy.setOnClickListener { openProducts("Dairy", "") }
        cardCategorySnacks.setOnClickListener { openProducts("Snacks", "") }
        cardCategoryDrinks.setOnClickListener { openProducts("Drinks", "") }
    }

    private fun openProducts(category: String, search: String) {
        val intent = Intent(this, CustomerProductsActivity::class.java).apply {
            putExtra("SELECTED_CATEGORY", category)
            putExtra("SEARCH_QUERY", search)
        }
        startActivity(intent)
    }
}