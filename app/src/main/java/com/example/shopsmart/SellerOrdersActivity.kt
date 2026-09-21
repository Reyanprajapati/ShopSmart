package com.example.shopsmart

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class SellerOrdersActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var tvSellerOrdersCount: TextView
    private lateinit var layoutSellerOrdersList: LinearLayout
    private lateinit var tvEmptySellerOrders: TextView
    private lateinit var orderPrefs: SharedPreferences

    private val statusSteps = arrayOf("Order Placed", "Confirmed", "Packed", "Out for Delivery", "Delivered")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_orders)

        initViews()
        initPrefs()
        setupListeners()
        loadOrders()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvSellerOrdersCount = findViewById(R.id.tvSellerOrdersCount)
        layoutSellerOrdersList = findViewById(R.id.layoutSellerOrdersList)
        tvEmptySellerOrders = findViewById(R.id.tvEmptySellerOrders)
    }

    private fun initPrefs() {
        orderPrefs = getSharedPreferences("ShopSmartOrders", Context.MODE_PRIVATE)
    }

    private fun setupListeners() {
        // WORKING BACK ARROW LISTENER
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadOrders() {
        layoutSellerOrdersList.removeAllViews()
        val jsonStr = orderPrefs.getString("ORDERS", "[]") ?: "[]"
        val orders = try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        tvSellerOrdersCount.text = "${orders.length()} Orders"
        tvEmptySellerOrders.visibility = if (orders.length() == 0) View.VISIBLE else View.GONE

        // Newest orders first
        for (i in (orders.length() - 1) downTo 0) {
            val order = orders.getJSONObject(i)
            val card = createOrderCard(order, i)
            layoutSellerOrdersList.addView(card)
        }
    }

    private fun createOrderCard(order: JSONObject, orderIndex: Int): View {
        val orderId = order.optString("orderId", "ORD#0")
        val customerName = order.optString("customerName", "Customer")
        val mobile = order.optString("mobile", "-")
        val address = order.optString("address", "-")
        val currentStatus = order.optString("status", "Order Placed")
        val payment = order.optString("payment", "Cash on Delivery")
        val total = order.optDouble("total", 0.0)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_card_white)
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }

        // Header Row: Order ID + Status
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val tvId = TextView(this).apply {
            text = "Order #$orderId"
            setTextColor(Color.parseColor("#6C63FF"))
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvStatus = TextView(this).apply {
            text = currentStatus
            setTextColor(Color.WHITE)
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setBackgroundResource(R.drawable.bg_badge_pill)
            setPadding(12, 4, 12, 4)
        }

        headerRow.addView(tvId)
        headerRow.addView(tvStatus)
        card.addView(headerRow)

        // Customer details
        val tvDetails = TextView(this).apply {
            text = "Customer: $customerName\nPhone: +91 $mobile\nAddress: $address\nPayment: $payment\nTotal: ₹${String.format("%.2f", total)}"
            setTextColor(Color.parseColor("#202124"))
            textSize = 13f
            setPadding(0, 10, 0, 10)
        }
        card.addView(tvDetails)

        // Update Status Button
        val btnUpdateStatus = Button(this).apply {
            text = "Update Status ➔"
            textSize = 13f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_login_button)
            setOnClickListener {
                advanceOrderStatus(orderIndex, currentStatus)
            }
        }
        card.addView(btnUpdateStatus)

        return card
    }

    private fun advanceOrderStatus(orderIndex: Int, currentStatus: String) {
        val jsonStr = orderPrefs.getString("ORDERS", "[]") ?: "[]"
        val orders = try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (orderIndex >= 0 && orderIndex < orders.length()) {
            val order = orders.getJSONObject(orderIndex)
            val currentIndex = statusSteps.indexOf(currentStatus)
            val nextIndex = if (currentIndex < statusSteps.size - 1) currentIndex + 1 else 0
            val nextStatus = statusSteps[nextIndex]

            order.put("status", nextStatus)
            orderPrefs.edit().putString("ORDERS", orders.toString()).apply()
            Toast.makeText(this, "Status updated to: $nextStatus", Toast.LENGTH_SHORT).show()
            loadOrders()
        }
    }
}