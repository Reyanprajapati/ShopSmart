package com.example.shopsmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class OrdersActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var layoutOrdersList: LinearLayout
    private lateinit var tvEmptyOrders: TextView
    private lateinit var orderPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        initViews()
        initPrefs()
        setupListeners()
        loadOrders()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        layoutOrdersList = findViewById(R.id.layoutOrdersList)
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders)
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
        layoutOrdersList.removeAllViews()
        val jsonStr = orderPrefs.getString("ORDERS", "[]") ?: "[]"
        val orders = try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (orders.length() == 0) {
            tvEmptyOrders.visibility = View.VISIBLE
            return
        }

        tvEmptyOrders.visibility = View.GONE

        // Newest first
        for (i in (orders.length() - 1) downTo 0) {
            val order = orders.getJSONObject(i)
            val card = createOrderCard(order)
            layoutOrdersList.addView(card)
        }
    }

    private fun createOrderCard(order: JSONObject): View {
        val orderId = order.optString("orderId", "ORD#0")
        val status = order.optString("status", "Order Placed")
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
            text = status
            setTextColor(Color.WHITE)
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setBackgroundResource(R.drawable.bg_badge_pill)
            setPadding(12, 4, 12, 4)
        }

        headerRow.addView(tvId)
        headerRow.addView(tvStatus)
        card.addView(headerRow)

        val tvInfo = TextView(this).apply {
            text = "Payment: $payment\nTotal Paid: ₹${String.format("%.2f", total)}"
            setTextColor(Color.parseColor("#202124"))
            textSize = 13f
            setPadding(0, 10, 0, 10)
        }
        card.addView(tvInfo)

        val buttonsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val btnTrack = Button(this).apply {
            text = "📍 Track Order"
            textSize = 12f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_login_button)
            layoutParams = LinearLayout.LayoutParams(0, 42, 1f).apply {
                marginEnd = 6
            }
            setOnClickListener {
                val intent = Intent(this@OrdersActivity, OrderTrackingActivity::class.java).apply {
                    putExtra("ORDER_ID", orderId)
                }
                startActivity(intent)
            }
        }

        val btnReview = Button(this).apply {
            text = "⭐ Review"
            textSize = 12f
            setTextColor(Color.parseColor("#6C63FF"))
            setBackgroundResource(R.drawable.bg_card_white)
            layoutParams = LinearLayout.LayoutParams(0, 42, 1f).apply {
                marginStart = 6
            }
            setOnClickListener {
                val intent = Intent(this@OrdersActivity, ReviewActivity::class.java).apply {
                    putExtra("ORDER_ID", orderId)
                }
                startActivity(intent)
            }
        }

        buttonsRow.addView(btnTrack)
        buttonsRow.addView(btnReview)
        card.addView(buttonsRow)

        return card
    }
}