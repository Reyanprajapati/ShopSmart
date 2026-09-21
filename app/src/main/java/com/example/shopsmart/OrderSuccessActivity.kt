package com.example.shopsmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class OrderSuccessActivity : ComponentActivity() {

    private lateinit var tvOrderId: TextView
    private lateinit var btnViewInvoice: Button
    private lateinit var btnMyOrders: Button
    private lateinit var btnContinueShopping: Button
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        orderId = intent.getStringExtra("ORDER_ID") ?: "ORD#0000"

        tvOrderId = findViewById(R.id.tvOrderId)
        btnViewInvoice = findViewById(R.id.btnViewInvoice)
        btnMyOrders = findViewById(R.id.btnMyOrders)
        btnContinueShopping = findViewById(R.id.btnContinueShopping)

        tvOrderId.text = orderId

        btnViewInvoice.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java).apply {
                putExtra("ORDER_ID", orderId)
            }
            startActivity(intent)
        }

        btnMyOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnContinueShopping.setOnClickListener {
            val intent = Intent(this, CustomerHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}