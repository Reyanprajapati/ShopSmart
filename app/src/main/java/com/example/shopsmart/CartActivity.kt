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
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var layoutCartItems: LinearLayout
    private lateinit var tvEmptyCart: TextView
    private lateinit var layoutBottomCheckout: LinearLayout
    private lateinit var tvCartTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var cartPrefs: SharedPreferences

    private var cartArray = JSONArray()
    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        initPrefs()
        setupListeners()
        loadCart()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        layoutCartItems = findViewById(R.id.layoutCartItems)
        tvEmptyCart = findViewById(R.id.tvEmptyCart)
        layoutBottomCheckout = findViewById(R.id.layoutBottomCheckout)
        tvCartTotal = findViewById(R.id.tvCartTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
    }

    private fun initPrefs() {
        cartPrefs = getSharedPreferences("ShopSmartCart", Context.MODE_PRIVATE)
    }

    private fun setupListeners() {
        // WORKING BACK ARROW LISTENER
        btnBack.setOnClickListener {
            finish()
        }

        btnCheckout.setOnClickListener {
            if (cartArray.length() == 0) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, CheckoutActivity::class.java))
            }
        }
    }

    private fun loadCart() {
        layoutCartItems.removeAllViews()
        val cartStr = cartPrefs.getString("CART", "[]") ?: "[]"
        cartArray = try {
            JSONArray(cartStr)
        } catch (e: Exception) {
            JSONArray()
        }

        totalAmount = 0.0

        if (cartArray.length() == 0) {
            tvEmptyCart.visibility = View.VISIBLE
            layoutBottomCheckout.visibility = View.GONE
            return
        }

        tvEmptyCart.visibility = View.GONE
        layoutBottomCheckout.visibility = View.VISIBLE

        for (i in 0 until cartArray.length()) {
            val item = cartArray.getJSONObject(i)
            val card = createCartItemCard(item, i)
            layoutCartItems.addView(card)

            val price = item.optDouble("price", 0.0)
            val qty = item.optInt("quantity", 1)
            totalAmount += (price * qty)
        }

        tvCartTotal.text = "₹${String.format("%.2f", totalAmount)}"
    }

    private fun createCartItemCard(item: JSONObject, index: Int): View {
        val name = item.optString("name", "Product")
        val price = item.optDouble("price", 0.0)
        val quantity = item.optInt("quantity", 1)
        val emoji = item.optString("emoji", "🛍️")

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

        val tvEmoji = TextView(this).apply {
            text = emoji
            textSize = 28f
            setPadding(0, 0, 12, 0)
        }

        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvName = TextView(this).apply {
            text = name
            setTextColor(Color.parseColor("#202124"))
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvPrice = TextView(this).apply {
            text = "₹$price x $quantity = ₹${String.format("%.2f", price * quantity)}"
            setTextColor(Color.parseColor("#6C63FF"))
            textSize = 13f
        }

        details.addView(tvName)
        details.addView(tvPrice)

        // Quantity controls
        val btnMinus = Button(this).apply {
            text = "-"
            textSize = 14f
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(40, 38)
            setOnClickListener { updateQuantity(index, -1) }
        }

        val tvQty = TextView(this).apply {
            text = "$quantity"
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(8, 0, 8, 0)
        }

        val btnPlus = Button(this).apply {
            text = "+"
            textSize = 14f
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(40, 38)
            setOnClickListener { updateQuantity(index, 1) }
        }

        val btnDelete = Button(this).apply {
            text = "🗑️"
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(40, 38)
            setOnClickListener { deleteItem(index) }
        }

        card.addView(tvEmoji)
        card.addView(details)
        card.addView(btnMinus)
        card.addView(tvQty)
        card.addView(btnPlus)
        card.addView(btnDelete)

        return card
    }

    private fun updateQuantity(index: Int, change: Int) {
        if (index in 0 until cartArray.length()) {
            val item = cartArray.getJSONObject(index)
            val currentQty = item.optInt("quantity", 1)
            val newQty = currentQty + change

            if (newQty <= 0) {
                cartArray.remove(index)
            } else {
                item.put("quantity", newQty)
            }

            cartPrefs.edit().putString("CART", cartArray.toString()).apply()
            loadCart()
        }
    }

    private fun deleteItem(index: Int) {
        if (index in 0 until cartArray.length()) {
            cartArray.remove(index)
            cartPrefs.edit().putString("CART", cartArray.toString()).apply()
            Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            loadCart()
        }
    }
}