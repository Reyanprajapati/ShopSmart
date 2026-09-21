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

class WishlistActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnGoToCart: TextView
    private lateinit var layoutWishlistItems: LinearLayout
    private lateinit var tvEmptyWishlist: TextView
    private lateinit var wishlistPrefs: SharedPreferences
    private lateinit var cartPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        initViews()
        initPrefs()
        setupListeners()
        loadWishlist()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnGoToCart = findViewById(R.id.btnGoToCart)
        layoutWishlistItems = findViewById(R.id.layoutWishlistItems)
        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist)
    }

    private fun initPrefs() {
        wishlistPrefs = getSharedPreferences("ShopSmartWishlist", Context.MODE_PRIVATE)
        cartPrefs = getSharedPreferences("ShopSmartCart", Context.MODE_PRIVATE)
    }

    private fun setupListeners() {
        // WORKING BACK ARROW LISTENER
        btnBack.setOnClickListener {
            finish()
        }

        btnGoToCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun loadWishlist() {
        layoutWishlistItems.removeAllViews()
        val wishStr = wishlistPrefs.getString("WISHLIST", "[]") ?: "[]"
        val wishArray = try {
            JSONArray(wishStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (wishArray.length() == 0) {
            tvEmptyWishlist.visibility = View.VISIBLE
            return
        }

        tvEmptyWishlist.visibility = View.GONE

        for (i in 0 until wishArray.length()) {
            val item = wishArray.getJSONObject(i)
            val card = createWishlistCard(item, i)
            layoutWishlistItems.addView(card)
        }
    }

    private fun createWishlistCard(item: JSONObject, index: Int): View {
        val name = item.optString("name", "Product")
        val price = item.optDouble("price", 0.0)
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
            text = "₹${String.format("%.2f", price)}"
            setTextColor(Color.parseColor("#6C63FF"))
            textSize = 13f
        }

        details.addView(tvName)
        details.addView(tvPrice)

        // Add To Cart Button
        val btnAddCart = Button(this).apply {
            text = "+ Cart"
            textSize = 11f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_login_button)
            layoutParams = LinearLayout.LayoutParams(70, 36).apply {
                marginEnd = 6
            }
            setOnClickListener {
                addItemToCart(item)
            }
        }

        // Delete from Wishlist
        val btnDelete = Button(this).apply {
            text = "✕"
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(40, 36)
            setOnClickListener {
                removeFromWishlist(index)
            }
        }

        card.addView(tvEmoji)
        card.addView(details)
        card.addView(btnAddCart)
        card.addView(btnDelete)

        return card
    }

    private fun addItemToCart(product: JSONObject) {
        val cartStr = cartPrefs.getString("CART", "[]") ?: "[]"
        val cartArray = try {
            JSONArray(cartStr)
        } catch (e: Exception) {
            JSONArray()
        }

        val name = product.optString("name")
        var found = false
        for (i in 0 until cartArray.length()) {
            if (cartArray.getJSONObject(i).optString("name") == name) {
                val currentQty = cartArray.getJSONObject(i).optInt("quantity", 1)
                cartArray.getJSONObject(i).put("quantity", currentQty + 1)
                found = true
                break
            }
        }

        if (!found) {
            val item = JSONObject().apply {
                put("name", name)
                put("price", product.optDouble("price", 0.0))
                put("quantity", 1)
                put("emoji", product.optString("emoji", "🛍️"))
            }
            cartArray.put(item)
        }

        cartPrefs.edit().putString("CART", cartArray.toString()).apply()
        Toast.makeText(this, "Moved to Cart 🛒", Toast.LENGTH_SHORT).show()
    }

    private fun removeFromWishlist(index: Int) {
        val wishStr = wishlistPrefs.getString("WISHLIST", "[]") ?: "[]"
        val wishArray = try {
            JSONArray(wishStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (index in 0 until wishArray.length()) {
            wishArray.remove(index)
            wishlistPrefs.edit().putString("WISHLIST", wishArray.toString()).apply()
            Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
            loadWishlist()
        }
    }
}