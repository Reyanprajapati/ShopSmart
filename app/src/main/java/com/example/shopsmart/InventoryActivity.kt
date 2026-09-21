package com.example.shopsmart

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class InventoryActivity : ComponentActivity() {

    private lateinit var btnBack: TextView
    private lateinit var tvTotalProductsCount: TextView
    private lateinit var tvFormTitle: TextView
    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductStock: EditText
    private lateinit var etProductEmoji: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSaveProduct: Button
    private lateinit var layoutInventoryList: LinearLayout
    private lateinit var tvEmptyInventory: TextView

    private lateinit var inventoryPrefs: SharedPreferences
    private var editingIndex = -1

    private val categoryOptions = arrayOf("Grocery", "Dairy", "Snacks", "Drinks", "Personal Care")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        initViews()
        initPrefs()
        setupSpinner()
        setupListeners()
        loadProducts()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTotalProductsCount = findViewById(R.id.tvTotalProductsCount)
        tvFormTitle = findViewById(R.id.tvFormTitle)
        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductStock = findViewById(R.id.etProductStock)
        etProductEmoji = findViewById(R.id.etProductEmoji)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSaveProduct = findViewById(R.id.btnSaveProduct)
        layoutInventoryList = findViewById(R.id.layoutInventoryList)
        tvEmptyInventory = findViewById(R.id.tvEmptyInventory)
    }

    private fun initPrefs() {
        inventoryPrefs = getSharedPreferences("ShopSmartInventory", Context.MODE_PRIVATE)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryOptions)
        spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSaveProduct.setOnClickListener {
            saveOrUpdateProduct()
        }
    }

    private fun loadProducts() {
        layoutInventoryList.removeAllViews()
        val jsonStr = inventoryPrefs.getString("PRODUCTS", "[]") ?: "[]"
        val products = try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        tvTotalProductsCount.text = "${products.length()} Items"
        tvEmptyInventory.visibility = if (products.length() == 0) View.VISIBLE else View.GONE

        for (i in 0 until products.length()) {
            val item = products.getJSONObject(i)
            val card = createInventoryCard(item, i)
            layoutInventoryList.addView(card)
        }
    }

    private fun createInventoryCard(item: JSONObject, index: Int): View {
        val name = item.optString("name", "Product")
        val price = item.optDouble("price", 0.0)
        val stock = item.optInt("stock", 0)
        val emoji = item.optString("emoji", "📦")
        val category = item.optString("category", "Grocery")

        val density = resources.displayMetrics.density

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.bg_card_white)
            gravity = Gravity.CENTER_VERTICAL
            setPadding((16 * density).toInt(), (14 * density).toInt(), (16 * density).toInt(), (14 * density).toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (12 * density).toInt()
            }
        }

        val tvEmoji = TextView(this).apply {
            text = emoji
            textSize = 28f
            setPadding(0, 0, (12 * density).toInt(), 0)
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

        val tvMeta = TextView(this).apply {
            text = "₹$price | Stock: $stock | $category"
            setTextColor(Color.parseColor("#757575"))
            textSize = 13f
        }

        details.addView(tvName)
        details.addView(tvMeta)

        // Properly Sized Edit Button
        val btnEdit = Button(this).apply {
            text = "Edit"
            textSize = 12f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_login_button)
            setPadding((14 * density).toInt(), 0, (14 * density).toInt(), 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (38 * density).toInt()
            ).apply {
                marginEnd = (6 * density).toInt()
            }
            setOnClickListener {
                startEditing(item, index)
            }
        }

        // Properly Sized Delete Button
        val btnDelete = Button(this).apply {
            text = "Del"
            textSize = 12f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_badge_pill)
            setPadding((14 * density).toInt(), 0, (14 * density).toInt(), 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (38 * density).toInt()
            )
            setOnClickListener {
                deleteProduct(index)
            }
        }

        card.addView(tvEmoji)
        card.addView(details)
        card.addView(btnEdit)
        card.addView(btnDelete)

        return card
    }

    private fun startEditing(item: JSONObject, index: Int) {
        editingIndex = index
        tvFormTitle.text = "Edit Product"
        etProductName.setText(item.optString("name"))
        etProductPrice.setText(item.optDouble("price").toString())
        etProductStock.setText(item.optInt("stock").toString())
        etProductEmoji.setText(item.optString("emoji"))

        val cat = item.optString("category", "Grocery")
        val catPos = categoryOptions.indexOf(cat)
        if (catPos >= 0) {
            spinnerCategory.setSelection(catPos)
        }

        btnSaveProduct.text = "Update Product"
    }

    private fun saveOrUpdateProduct() {
        val name = etProductName.text.toString().trim()
        val priceStr = etProductPrice.text.toString().trim()
        val stockStr = etProductStock.text.toString().trim()
        val emoji = etProductEmoji.text.toString().trim().ifEmpty { "📦" }
        val category = spinnerCategory.selectedItem?.toString() ?: "Grocery"

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 0

        val jsonStr = inventoryPrefs.getString("PRODUCTS", "[]") ?: "[]"
        val products = try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        val obj = JSONObject().apply {
            put("name", name)
            put("price", price)
            put("stock", stock)
            put("emoji", emoji)
            put("category", category)
        }

        if (editingIndex >= 0 && editingIndex < products.length()) {
            products.put(editingIndex, obj)
            Toast.makeText(this, "Product Updated!", Toast.LENGTH_SHORT).show()
        } else {
            products.put(obj)
            Toast.makeText(this, "Product Added!", Toast.LENGTH_SHORT).show()
        }

        inventoryPrefs.edit().putString("PRODUCTS", products.toString()).apply()

        // Reset form
        editingIndex = -1
        tvFormTitle.text = "Add New Product"
        btnSaveProduct.text = "+ Save Product"
        etProductName.text.clear()
        etProductPrice.text.clear()
        etProductStock.text.clear()
        etProductEmoji.text.clear()
        spinnerCategory.setSelection(0)

        loadProducts()
    }

    private fun deleteProduct(index: Int) {
        val jsonStr = inventoryPrefs.getString("PRODUCTS", "[]") ?: "[]"
        val products = try {
            JSONArray(jsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        if (index >= 0 && index < products.length()) {
            products.remove(index)
            inventoryPrefs.edit().putString("PRODUCTS", products.toString()).apply()
            Toast.makeText(this, "Product Deleted!", Toast.LENGTH_SHORT).show()
            loadProducts()
        }
    }
}