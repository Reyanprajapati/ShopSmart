package com.example.shopsmart

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class NotificationsActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var tvNotificationCount: TextView
    private lateinit var layoutNotificationsList: LinearLayout
    private lateinit var tvEmptyNotifications: TextView

    private lateinit var orderPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        initViews()
        initPrefs()
        setupListeners()
        loadNotifications()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvNotificationCount = findViewById(R.id.tvNotificationCount)
        layoutNotificationsList = findViewById(R.id.layoutNotificationsList)
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications)
    }

    private fun initPrefs() {
        orderPrefs = getSharedPreferences("ShopSmartOrders", Context.MODE_PRIVATE)
    }

    private fun setupListeners() {
        // 1. WORKING BACK ARROW LISTENER
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadNotifications() {
        layoutNotificationsList.removeAllViews()
        val ordersStr = orderPrefs.getString("ORDERS", "[]") ?: "[]"
        val ordersArray = try {
            JSONArray(ordersStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (ordersArray.length() == 0) {
            tvEmptyNotifications.visibility = View.VISIBLE
            tvNotificationCount.text = "0 Alerts"
            return
        }

        tvEmptyNotifications.visibility = View.GONE
        tvNotificationCount.text = "${ordersArray.length()} Alerts"

        // Newest notifications first
        for (i in (ordersArray.length() - 1) downTo 0) {
            val order = ordersArray.getJSONObject(i)
            val card = createNotificationCard(order)
            layoutNotificationsList.addView(card)
        }
    }

    private fun createNotificationCard(order: JSONObject): View {
        val orderId = order.optString("orderId", "ORD#0")
        val status = order.optString("status", "Order Placed")
        val customerName = order.optString("customerName", "Customer")
        val total = order.optDouble("total", 0.0)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.bg_card_white)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        // Notification Icon
        val tvIcon = TextView(this).apply {
            text = when (status) {
                "Delivered" -> "🎉"
                "Out for Delivery" -> "🚚"
                "Packed" -> "📦"
                "Confirmed" -> "✅"
                else -> "🔔"
            }
            textSize = 28f
            setPadding(0, 0, 14, 0)
        }

        // Details
        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvTitle = TextView(this).apply {
            text = "Order #$orderId: $status"
            setTextColor(Color.parseColor("#202124"))
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvSub = TextView(this).apply {
            text = "Order for $customerName worth ₹${String.format("%.2f", total)}"
            setTextColor(Color.parseColor("#757575"))
            textSize = 13f
            setPadding(0, 4, 0, 0)
        }

        details.addView(tvTitle)
        details.addView(tvSub)

        card.addView(tvIcon)
        card.addView(details)

        return card
    }
}