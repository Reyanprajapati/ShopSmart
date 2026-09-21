package com.example.shopsmart

import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.json.JSONArray

class OrderTrackingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_order_tracking
        )

        val orderId =
            intent.getStringExtra(
                "ORDER_ID"
            ) ?: ""

        findViewById<TextView>(
            R.id.btnTrackingBack
        ).setOnClickListener {
            finish()
        }

        loadTracking(orderId)
    }

    private fun loadTracking(
        orderId: String
    ) {

        val container =
            findViewById<LinearLayout>(
                R.id.trackingContainer
            )

        val preferences =
            getSharedPreferences(
                "ShopSmartOrders",
                MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "ORDERS",
                "[]"
            ) ?: "[]"

        val orders =
            try {
                JSONArray(data)
            } catch (e: Exception) {
                JSONArray()
            }

        var status =
            "Order Placed"

        for (i in 0 until orders.length()) {

            val order =
                orders.getJSONObject(i)

            if (
                order.optString(
                    "orderId"
                ) == orderId
            ) {

                status =
                    order.optString(
                        "status",
                        "Order Placed"
                    )

                break
            }
        }

        val steps =
            listOf(
                "Order Placed",
                "Confirmed",
                "Packed",
                "Out for Delivery",
                "Delivered"
            )

        val currentIndex =
            steps.indexOf(status)

        for (i in steps.indices) {

            val item =
                TextView(this)

            if (i <= currentIndex) {

                item.text =
                    "✅  ${steps[i]}"

            } else {

                item.text =
                    "○  ${steps[i]}"
            }

            item.textSize = 18f

            item.setPadding(
                20,
                25,
                20,
                25
            )

            if (i <= currentIndex) {

                item.setTypeface(
                    null,
                    Typeface.BOLD
                )
            }

            container.addView(item)
        }
    }
}