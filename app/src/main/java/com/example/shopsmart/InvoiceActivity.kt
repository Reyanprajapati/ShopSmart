package com.example.shopsmart

import android.content.Context
import android.content.Intent
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

class InvoiceActivity : ComponentActivity() {

    private lateinit var tvInvoiceOrderId: TextView
    private lateinit var tvInvoiceStatus: TextView
    private lateinit var tvInvoiceCustomer: TextView
    private lateinit var tvInvoiceMobile: TextView
    private lateinit var tvInvoiceAddress: TextView
    private lateinit var tvInvoicePayment: TextView
    private lateinit var layoutInvoiceItems: LinearLayout
    private lateinit var tvInvoiceGrandTotal: TextView
    private lateinit var btnDoneInvoice: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        initViews()
        val orderId = intent.getStringExtra("ORDER_ID") ?: ""
        loadInvoiceData(orderId)

        btnDoneInvoice.setOnClickListener {
            val intent = Intent(this, CustomerHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        tvInvoiceOrderId = findViewById(R.id.tvInvoiceOrderId)
        tvInvoiceStatus = findViewById(R.id.tvInvoiceStatus)
        tvInvoiceCustomer = findViewById(R.id.tvInvoiceCustomer)
        tvInvoiceMobile = findViewById(R.id.tvInvoiceMobile)
        tvInvoiceAddress = findViewById(R.id.tvInvoiceAddress)
        tvInvoicePayment = findViewById(R.id.tvInvoicePayment)
        layoutInvoiceItems = findViewById(R.id.layoutInvoiceItems)
        tvInvoiceGrandTotal = findViewById(R.id.tvInvoiceGrandTotal)
        btnDoneInvoice = findViewById(R.id.btnDoneInvoice)
    }

    private fun loadInvoiceData(targetOrderId: String) {
        val prefs = getSharedPreferences("ShopSmartOrders", Context.MODE_PRIVATE)
        val ordersStr = prefs.getString("ORDERS", "[]") ?: "[]"

        val ordersArray = try {
            JSONArray(ordersStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (ordersArray.length() == 0) {
            Toast.makeText(this, "No orders found!", Toast.LENGTH_SHORT).show()
            return
        }

        // Find target order or take newest
        var selectedOrder: JSONObject? = null
        if (targetOrderId.isNotEmpty()) {
            for (i in 0 until ordersArray.length()) {
                val order = ordersArray.getJSONObject(i)
                if (order.optString("orderId") == targetOrderId) {
                    selectedOrder = order
                    break
                }
            }
        }

        if (selectedOrder == null) {
            selectedOrder = ordersArray.getJSONObject(ordersArray.length() - 1)
        }

        // Populate Views
        tvInvoiceOrderId.text = "Order #: ${selectedOrder.optString("orderId", "N/A")}"
        tvInvoiceStatus.text = selectedOrder.optString("status", "Order Placed")
        tvInvoiceCustomer.text = selectedOrder.optString("customerName", "Customer")
        tvInvoiceMobile.text = "Phone: +91 ${selectedOrder.optString("mobile", "-")}"
        tvInvoiceAddress.text = selectedOrder.optString("address", "-")
        tvInvoicePayment.text = selectedOrder.optString("payment", "Cash on Delivery")

        val total = selectedOrder.optDouble("total", 0.0)
        tvInvoiceGrandTotal.text = "₹${String.format("%.2f", total)}"

        // Populate Items Table
        layoutInvoiceItems.removeAllViews()
        val itemsArray = selectedOrder.optJSONArray("items") ?: JSONArray()

        for (i in 0 until itemsArray.length()) {
            val item = itemsArray.getJSONObject(i)
            val name = item.optString("name", "Item")
            val qty = item.optInt("quantity", 1)
            val price = item.optDouble("price", 0.0)
            val itemTotal = price * qty

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(8, 12, 8, 12)
            }

            val tvItemName = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                text = name
                setTextColor(Color.parseColor("#202124"))
                textSize = 13f
            }

            val tvItemQty = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "x$qty"
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#757575"))
                textSize = 13f
            }

            val tvItemTotal = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "₹${String.format("%.2f", itemTotal)}"
                gravity = Gravity.END
                setTextColor(Color.parseColor("#202124"))
                textSize = 13f
            }

            row.addView(tvItemName)
            row.addView(tvItemQty)
            row.addView(tvItemTotal)

            layoutInvoiceItems.addView(row)

            // Divider between rows
            if (i < itemsArray.length() - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                    )
                    setBackgroundColor(Color.parseColor("#F5F5F5"))
                }
                layoutInvoiceItems.addView(divider)
            }
        }
    }
}