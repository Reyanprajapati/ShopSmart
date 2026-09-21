package com.example.shopsmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class CustomerProductsActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnWishlist: TextView
    private lateinit var btnCart: TextView
    private lateinit var etSearchProducts: EditText
    private lateinit var layoutProductsList: LinearLayout
    private lateinit var tvNoProducts: TextView

    private lateinit var chipAll: TextView
    private lateinit var chipGrocery: TextView
    private lateinit var chipDairy: TextView
    private lateinit var chipSnacks: TextView
    private lateinit var chipBeverages: TextView

    private lateinit var inventoryPrefs: SharedPreferences
    private lateinit var cartPrefs: SharedPreferences
    private lateinit var wishlistPrefs: SharedPreferences

    private var allProducts = JSONArray()
    private var selectedCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_products)

        initViews()
        initPrefs()
        setupTopListeners()
        loadProductsData()

        val searchQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""
        val categoryFromHome = intent.getStringExtra("SELECTED_CATEGORY") ?: "All"

        if (searchQuery.isNotEmpty()) {
            etSearchProducts.setText(searchQuery)
        }

        // Apply initial category if passed from home
        val chips = listOf(chipAll, chipGrocery, chipDairy, chipSnacks, chipBeverages)
        when (categoryFromHome) {
            "Grocery" -> updateCategory("Grocery", chips, chipGrocery)
            "Dairy" -> updateCategory("Dairy", chips, chipDairy)
            "Snacks" -> updateCategory("Snacks", chips, chipSnacks)
            "Drinks" -> updateCategory("Drinks", chips, chipBeverages)
            else -> updateCategory("All", chips, chipAll)
        }
    }

    override fun onResume() {
        super.onResume()
        loadProductsData()
        filterAndDisplayProducts(etSearchProducts.text.toString().trim(), selectedCategory)
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnWishlist = findViewById(R.id.btnWishlist)
        btnCart = findViewById(R.id.btnCart)
        etSearchProducts = findViewById(R.id.etSearchProducts)
        layoutProductsList = findViewById(R.id.layoutProductsList)
        tvNoProducts = findViewById(R.id.tvNoProducts)

        chipAll = findViewById(R.id.chipAll)
        chipGrocery = findViewById(R.id.chipGrocery)
        chipDairy = findViewById(R.id.chipDairy)
        chipSnacks = findViewById(R.id.chipSnacks)
        chipBeverages = findViewById(R.id.chipBeverages)
    }

    private fun initPrefs() {
        inventoryPrefs = getSharedPreferences("ShopSmartInventory", Context.MODE_PRIVATE)
        cartPrefs = getSharedPreferences("ShopSmartCart", Context.MODE_PRIVATE)
        wishlistPrefs = getSharedPreferences("ShopSmartWishlist", Context.MODE_PRIVATE)
    }

    private fun setupTopListeners() {
        // Back Arrow
        btnBack.setOnClickListener {
            finish()
        }

        // Wishlist
        btnWishlist.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        // Cart
        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Search Filter
        etSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAndDisplayProducts(s.toString().trim(), selectedCategory)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        // Category Chips Listeners
        val chips = listOf(chipAll, chipGrocery, chipDairy, chipSnacks, chipBeverages)
        chipAll.setOnClickListener { updateCategory("All", chips, chipAll) }
        chipGrocery.setOnClickListener { updateCategory("Grocery", chips, chipGrocery) }
        chipDairy.setOnClickListener { updateCategory("Dairy", chips, chipDairy) }
        chipSnacks.setOnClickListener { updateCategory("Snacks", chips, chipSnacks) }
        chipBeverages.setOnClickListener { updateCategory("Drinks", chips, chipBeverages) }
    }

    private fun updateCategory(category: String, chips: List<TextView>, activeChip: TextView) {
        selectedCategory = category
        chips.forEach {
            it.setBackgroundResource(R.drawable.bg_chip)
            it.setTextColor(Color.parseColor("#202124"))
        }
        activeChip.setBackgroundResource(R.drawable.bg_badge_pill)
        activeChip.setTextColor(Color.WHITE)

        filterAndDisplayProducts(etSearchProducts.text.toString().trim(), selectedCategory)
    }

    private fun loadProductsData() {
        val jsonStr = inventoryPrefs.getString("PRODUCTS", "") ?: ""
        if (jsonStr.isEmpty() || jsonStr == "[]") {
            allProducts = JSONArray().apply {
                put(JSONObject().apply {
                    put("name", "Fresh Cow Milk")
                    put("price", 35.0)
                    put("stock", 25)
                    put("emoji", "🥛")
                    put("category", "Dairy")
                })
                put(JSONObject().apply {
                    put("name", "Basmati Rice (1kg)")
                    put("price", 85.0)
                    put("stock", 20)
                    put("emoji", "🌾")
                    put("category", "Grocery")
                })
                put(JSONObject().apply {
                    put("name", "Chocolate Cookies")
                    put("price", 40.0)
                    put("stock", 30)
                    put("emoji", "🍪")
                    put("category", "Snacks")
                })
                put(JSONObject().apply {
                    put("name", "Fresh Orange Juice")
                    put("price", 50.0)
                    put("stock", 15)
                    put("emoji", "🧃")
                    put("category", "Drinks")
                })
                put(JSONObject().apply {
                    put("name", "Wheat Flour (Atta 5kg)")
                    put("price", 210.0)
                    put("stock", 12)
                    put("emoji", "🌾")
                    put("category", "Grocery")
                })
                put(JSONObject().apply {
                    put("name", "Fresh Butter")
                    put("price", 55.0)
                    put("stock", 18)
                    put("emoji", "🧈")
                    put("category", "Dairy")
                })
            }
            inventoryPrefs.edit().putString("PRODUCTS", allProducts.toString()).apply()
        } else {
            allProducts = try {
                JSONArray(jsonStr)
            } catch (e: Exception) {
                JSONArray()
            }
        }
    }

    private fun filterAndDisplayProducts(query: String, category: String) {
        layoutProductsList.removeAllViews()
        var matchCount = 0

        val wishlistItems = getWishlistItems()

        for (i in 0 until allProducts.length()) {
            val product = allProducts.getJSONObject(i)
            val name = product.optString("name", "")
            val prodCategory = product.optString("category", "General")
            val price = product.optDouble("price", 0.0)
            val stock = product.optInt("stock", 0)
            val emoji = product.optString("emoji", "🛍️")

            val matchesQuery = query.isEmpty() || name.contains(query, ignoreCase = true)
            val matchesCat = matchesSelectedCategory(category, name, prodCategory)

            if (matchesQuery && matchesCat) {
                matchCount++
                val isWishlisted = wishlistItems.contains(name)
                val card = createProductCard(name, price, stock, emoji, prodCategory, isWishlisted, product)
                layoutProductsList.addView(card)
            }
        }

        tvNoProducts.visibility = if (matchCount == 0) View.VISIBLE else View.GONE
    }

    // Smart Accurate Category Matcher
    private fun matchesSelectedCategory(filterCat: String, name: String, itemCategory: String): Boolean {
        if (filterCat == "All") return true

        val lowerName = name.lowercase()
        val lowerCat = itemCategory.lowercase()

        return when (filterCat) {
            "Dairy" -> {
                lowerCat.contains("dairy") || lowerCat.contains("milk") ||
                        lowerName.contains("milk") || lowerName.contains("butter") ||
                        lowerName.contains("cheese") || lowerName.contains("paneer") ||
                        lowerName.contains("curd") || lowerName.contains("ghee")
            }
            "Grocery" -> {
                // Ignore dairy items even if accidentally saved as grocery
                if (lowerName.contains("milk") || lowerName.contains("butter") || lowerName.contains("cheese")) {
                    return false
                }
                lowerCat.contains("grocery") || lowerCat.contains("grain") ||
                        lowerName.contains("rice") || lowerName.contains("atta") ||
                        lowerName.contains("flour") || lowerName.contains("dal") ||
                        lowerName.contains("oil") || lowerName.contains("sugar") ||
                        lowerName.contains("salt") || lowerName.contains("wheat")
            }
            "Snacks" -> {
                lowerCat.contains("snack") || lowerCat.contains("biscuit") ||
                        lowerName.contains("cookie") || lowerName.contains("chips") ||
                        lowerName.contains("chocolate") || lowerName.contains("namkeen")
            }
            "Drinks" -> {
                lowerCat.contains("drink") || lowerCat.contains("beverage") || lowerCat.contains("juice") ||
                        lowerName.contains("juice") || lowerName.contains("tea") ||
                        lowerName.contains("coffee") || lowerName.contains("soda") ||
                        lowerName.contains("cola")
            }
            else -> {
                lowerCat.contains(filterCat.lowercase()) || lowerName.contains(filterCat.lowercase())
            }
        }
    }

    private fun createProductCard(
        name: String,
        price: Double,
        stock: Int,
        emoji: String,
        category: String,
        isWishlisted: Boolean,
        productJson: JSONObject
    ): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.bg_card_white)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(24, 20, 24, 20)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }

        val tvEmoji = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 16
            }
            text = emoji
            textSize = 30f
            gravity = Gravity.CENTER
        }

        val layoutDetails = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvName = TextView(this).apply {
            text = name
            setTextColor(Color.parseColor("#202124"))
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvStock = TextView(this).apply {
            if (stock > 5) {
                text = "🟢 In Stock ($stock available)"
                setTextColor(Color.parseColor("#00C9A7"))
            } else if (stock in 1..5) {
                text = "🟠 Only $stock left!"
                setTextColor(Color.parseColor("#FF9F43"))
            } else {
                text = "🔴 Out of Stock"
                setTextColor(Color.parseColor("#FF6B9A"))
            }
            textSize = 11f
        }

        val tvPrice = TextView(this).apply {
            text = "₹${String.format("%.2f", price)}"
            setTextColor(Color.parseColor("#6C63FF"))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 4, 0, 0)
        }

        layoutDetails.addView(tvName)
        layoutDetails.addView(tvStock)
        layoutDetails.addView(tvPrice)

        val layoutActions = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
        }

        val tvHeart = TextView(this).apply {
            text = if (isWishlisted) "❤️" else "🤍"
            textSize = 20f
            setPadding(8, 0, 8, 8)
            setOnClickListener {
                toggleWishlist(name, productJson)
                text = if (getWishlistItems().contains(name)) "❤️" else "🤍"
            }
        }

        val btnAdd = Button(this).apply {
            text = "+ Add"
            textSize = 12f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_login_button)
            setOnClickListener {
                if (stock <= 0) {
                    Toast.makeText(this@CustomerProductsActivity, "Item is out of stock!", Toast.LENGTH_SHORT).show()
                } else {
                    addToCart(productJson)
                }
            }
        }

        layoutActions.addView(tvHeart)
        layoutActions.addView(btnAdd)

        card.addView(tvEmoji)
        card.addView(layoutDetails)
        card.addView(layoutActions)

        return card
    }

    private fun addToCart(product: JSONObject) {
        val cartStr = cartPrefs.getString("CART", "[]") ?: "[]"
        val cartArray = try {
            JSONArray(cartStr)
        } catch (e: Exception) {
            JSONArray()
        }

        val name = product.optString("name")
        var existingIndex = -1

        for (i in 0 until cartArray.length()) {
            if (cartArray.getJSONObject(i).optString("name") == name) {
                existingIndex = i
                break
            }
        }

        if (existingIndex != -1) {
            val existing = cartArray.getJSONObject(existingIndex)
            val currentQty = existing.optInt("quantity", 1)
            existing.put("quantity", currentQty + 1)
        } else {
            val item = JSONObject().apply {
                put("name", name)
                put("price", product.optDouble("price", 0.0))
                put("quantity", 1)
                put("emoji", product.optString("emoji", "🛍️"))
            }
            cartArray.put(item)
        }

        cartPrefs.edit().putString("CART", cartArray.toString()).apply()
        Toast.makeText(this, "Added $name to Cart 🛒", Toast.LENGTH_SHORT).show()
    }

    private fun toggleWishlist(name: String, product: JSONObject) {
        val wishStr = wishlistPrefs.getString("WISHLIST", "[]") ?: "[]"
        val wishArray = try {
            JSONArray(wishStr)
        } catch (e: Exception) {
            JSONArray()
        }

        var foundIndex = -1
        for (i in 0 until wishArray.length()) {
            if (wishArray.getJSONObject(i).optString("name") == name) {
                foundIndex = i
                break
            }
        }

        if (foundIndex != -1) {
            wishArray.remove(foundIndex)
            Toast.makeText(this, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
        } else {
            wishArray.put(product)
            Toast.makeText(this, "Saved to Wishlist ❤️", Toast.LENGTH_SHORT).show()
        }

        wishPrefsEdit(wishArray.toString())
    }

    private fun getWishlistItems(): MutableSet<String> {
        val wishStr = wishlistPrefs.getString("WISHLIST", "[]") ?: "[]"
        val items = mutableSetOf<String>()
        try {
            val arr = JSONArray(wishStr)
            for (i in 0 until arr.length()) {
                items.add(arr.getJSONObject(i).optString("name"))
            }
        } catch (_: Exception) {}
        return items
    }

    private fun wishPrefsEdit(data: String) {
        wishlistPrefs.edit().putString("WISHLIST", data).apply()
    }
}