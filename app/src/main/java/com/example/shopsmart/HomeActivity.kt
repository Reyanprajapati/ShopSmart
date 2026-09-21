package com.example.shopsmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.json.JSONArray

class HomeActivity : ComponentActivity() {

    private lateinit var tvSellerGreeting: TextView
    private lateinit var btnNotifications: Button
    private lateinit var btnProfile: Button

    private lateinit var tvProductCount: TextView
    private lateinit var tvOrderCount: TextView
    private lateinit var tvSalesTotal: TextView

    private lateinit var cardStatProducts: LinearLayout
    private lateinit var cardStatOrders: LinearLayout
    private lateinit var cardStatSales: LinearLayout

    private lateinit var cardInventoryShortcut: LinearLayout
    private lateinit var btnOpenInventory: Button
    private lateinit var cardOrdersShortcut: LinearLayout
    private lateinit var btnOpenOrders: Button
    private lateinit var cardCustomerPreview: LinearLayout

    private lateinit var prefs: SharedPreferences
    private lateinit var inventoryPrefs: SharedPreferences
    private lateinit var orderPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        initPrefs()
        setupListeners()
        loadDashboardData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh live stats when returning from Inventory or Orders
        loadDashboardData()
    }

    private fun initViews() {
        tvSellerGreeting = findViewById(R.id.tvSellerGreeting)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnProfile = findViewById(R.id.btnProfile)

        tvProductCount = findViewById(R.id.tvProductCount)
        tvOrderCount = findViewById(R.id.tvOrderCount)
        tvSalesTotal = findViewById(R.id.tvSalesTotal)

        cardStatProducts = findViewById(R.id.cardStatProducts)
        cardStatOrders = findViewById(R.id.cardStatOrders)
        cardStatSales = findViewById(R.id.cardStatSales)

        cardInventoryShortcut = findViewById(R.id.cardInventoryShortcut)
        btnOpenInventory = findViewById(R.id.btnOpenInventory)
        cardOrdersShortcut = findViewById(R.id.cardOrdersShortcut)
        btnOpenOrders = findViewById(R.id.btnOpenOrders)
        cardCustomerPreview = findViewById(R.id.cardCustomerPreview)
    }

    private fun initPrefs() {
        prefs = getSharedPreferences("ShopSmartPrefs", Context.MODE_PRIVATE)
        inventoryPrefs = getSharedPreferences("ShopSmartInventory", Context.MODE_PRIVATE)
        orderPrefs = getSharedPreferences("ShopSmartOrders", Context.MODE_PRIVATE)
    }

    private fun loadDashboardData() {
        // Welcome Username
        val username = prefs.getString("USERNAME", "Seller") ?: "Seller"
        tvSellerGreeting.text = "Welcome, $username 👋"

        // 1. Live Products Count
        val inventoryStr = inventoryPrefs.getString("PRODUCTS", "[]") ?: "[]"
        val productsArray = try {
            JSONArray(inventoryStr)
        } catch (e: Exception) {
            JSONArray()
        }
        tvProductCount.text = "${productsArray.length()}"

        // 2. Orders Count & Smart Total Revenue Calculation
        val ordersStr = orderPrefs.getString("ORDERS", "[]") ?: "[]"
        val ordersArray = try {
            JSONArray(ordersStr)
        } catch (e: Exception) {
            JSONArray()
        }
        tvOrderCount.text = "${ordersArray.length()}"

        var totalSales = 0.0
        for (i in 0 until ordersArray.length()) {
            val order = ordersArray.getJSONObject(i)

            // Step A: Try direct double
            var orderTotal = order.optDouble("total", 0.0)

            // Step B: Try string parse if it had '₹'
            if (orderTotal == 0.0) {
                val totalStr = order.optString("total", "0").replace("₹", "").trim()
                orderTotal = totalStr.toDoubleOrNull() ?: 0.0
            }

            // Step C: Fallback to summing items
            if (orderTotal == 0.0) {
                val items = order.optJSONArray("items") ?: JSONArray()
                for (j in 0 until items.length()) {
                    val item = items.getJSONObject(j)
                    val price = item.optDouble("price", 0.0)
                    val qty = item.optInt("quantity", 1)
                    orderTotal += (price * qty)
                }
            }

            totalSales += orderTotal
        }

        tvSalesTotal.text = "₹${String.format("%.2f", totalSales)}"
    }

    private fun setupListeners() {
        // 1. Top Action Buttons
        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 2. Clickable Metric Cards
        cardStatProducts.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        cardStatOrders.setOnClickListener {
            startActivity(Intent(this, SellerOrdersActivity::class.java))
        }

        cardStatSales.setOnClickListener {
            startActivity(Intent(this, SellerOrdersActivity::class.java))
        }

        // 3. Inventory Management (Card & Button)
        cardInventoryShortcut.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        btnOpenInventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // 4. Customer Orders Dispatch (Card & Button)
        cardOrdersShortcut.setOnClickListener {
            startActivity(Intent(this, SellerOrdersActivity::class.java))
        }
        btnOpenOrders.setOnClickListener {
            startActivity(Intent(this, SellerOrdersActivity::class.java))
        }

        // 5. Preview Store as Customer Shortcut
        cardCustomerPreview.setOnClickListener {
            startActivity(Intent(this, CustomerProductsActivity::class.java))
        }
    }
}