package com.example.shopsmart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class CheckoutActivity : ComponentActivity() {

    private lateinit var etName: EditText
    private lateinit var etMobile: EditText
    private lateinit var etAddress: EditText
    private lateinit var rgPaymentMethod: RadioGroup
    private lateinit var rbCod: RadioButton
    private lateinit var rbUpi: RadioButton
    private lateinit var rbCard: RadioButton
    private lateinit var layoutUpiDetails: LinearLayout
    private lateinit var layoutCardDetails: LinearLayout
    private lateinit var etUpiId: EditText
    private lateinit var etCardNumber: EditText
    private lateinit var etCardExpiry: EditText
    private lateinit var etCardCvv: EditText
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: Button

    private lateinit var cartPrefs: SharedPreferences
    private lateinit var orderPrefs: SharedPreferences
    private lateinit var inventoryPrefs: SharedPreferences

    private var totalAmount: Double = 0.0
    private var cartItemsArray: JSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initViews()
        initPrefs()
        loadCartData()
        setupPaymentListeners()

        btnPlaceOrder.setOnClickListener {
            validateAndPlaceOrder()
        }
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etMobile = findViewById(R.id.etMobile)
        etAddress = findViewById(R.id.etAddress)
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod)
        rbCod = findViewById(R.id.rbCod)
        rbUpi = findViewById(R.id.rbUpi)
        rbCard = findViewById(R.id.rbCard)
        layoutUpiDetails = findViewById(R.id.layoutUpiDetails)
        layoutCardDetails = findViewById(R.id.layoutCardDetails)
        etUpiId = findViewById(R.id.etUpiId)
        etCardNumber = findViewById(R.id.etCardNumber)
        etCardExpiry = findViewById(R.id.etCardExpiry)
        etCardCvv = findViewById(R.id.etCardCvv)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
    }

    private fun initPrefs() {
        cartPrefs = getSharedPreferences("ShopSmartCart", Context.MODE_PRIVATE)
        orderPrefs = getSharedPreferences("ShopSmartOrders", Context.MODE_PRIVATE)
        inventoryPrefs = getSharedPreferences("ShopSmartInventory", Context.MODE_PRIVATE)
    }

    private fun loadCartData() {
        val cartJsonStr = cartPrefs.getString("CART", "[]") ?: "[]"
        cartItemsArray = try {
            JSONArray(cartJsonStr)
        } catch (e: Exception) {
            JSONArray()
        }

        totalAmount = 0.0
        for (i in 0 until cartItemsArray.length()) {
            val item = cartItemsArray.getJSONObject(i)
            val price = item.optDouble("price", 0.0)
            val quantity = item.optInt("quantity", 1)
            totalAmount += (price * quantity)
        }

        tvTotalAmount.text = "₹${String.format("%.2f", totalAmount)}"
    }

    private fun setupPaymentListeners() {
        rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCod -> {
                    layoutUpiDetails.visibility = View.GONE
                    layoutCardDetails.visibility = View.GONE
                }
                R.id.rbUpi -> {
                    layoutUpiDetails.visibility = View.VISIBLE
                    layoutCardDetails.visibility = View.GONE
                }
                R.id.rbCard -> {
                    layoutUpiDetails.visibility = View.GONE
                    layoutCardDetails.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateAndPlaceOrder() {
        val name = etName.text.toString().trim()
        val mobile = etMobile.text.toString().trim()
        val address = etAddress.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Please enter your name"
            etName.requestFocus()
            return
        }

        if (mobile.length != 10) {
            etMobile.error = "Please enter valid 10-digit mobile number"
            etMobile.requestFocus()
            return
        }

        if (address.isEmpty()) {
            etAddress.error = "Please enter complete address"
            etAddress.requestFocus()
            return
        }

        if (cartItemsArray.length() == 0) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        // Payment Method determination & validation
        val paymentMethodStr: String
        when {
            rbCod.isChecked -> {
                paymentMethodStr = "Cash on Delivery"
            }
            rbUpi.isChecked -> {
                val upiId = etUpiId.text.toString().trim()
                if (upiId.isEmpty() || !upiId.contains("@")) {
                    etUpiId.error = "Enter valid UPI ID (e.g. yourname@upi)"
                    etUpiId.requestFocus()
                    return
                }
                paymentMethodStr = "UPI ($upiId)"
            }
            rbCard.isChecked -> {
                val cardNumber = etCardNumber.text.toString().trim()
                val cardExpiry = etCardExpiry.text.toString().trim()
                val cardCvv = etCardCvv.text.toString().trim()

                if (cardNumber.length != 16) {
                    etCardNumber.error = "Enter valid 16-digit card number"
                    etCardNumber.requestFocus()
                    return
                }

                if (cardExpiry.length < 4 || !cardExpiry.contains("/")) {
                    etCardExpiry.error = "Enter valid MM/YY"
                    etCardExpiry.requestFocus()
                    return
                }

                if (cardCvv.length != 3) {
                    etCardCvv.error = "Enter 3-digit CVV"
                    etCardCvv.requestFocus()
                    return
                }

                val last4 = cardNumber.takeLast(4)
                paymentMethodStr = "Card (Ending with $last4)"
            }
            else -> {
                paymentMethodStr = "Cash on Delivery"
            }
        }

        // Check & Update Stock in Seller Inventory
        if (!deductInventoryStock()) {
            return
        }

        // Generate Order ID & Save Order
        val orderId = "ORD${System.currentTimeMillis() % 1000000}"
        saveOrder(orderId, name, mobile, address, paymentMethodStr)

        // Clear Cart
        cartPrefs.edit().remove("CART").apply()

        // Open OrderSuccessActivity
        val intent = Intent(this, OrderSuccessActivity::class.java).apply {
            putExtra("ORDER_ID", orderId)
        }
        startActivity(intent)
        finish()
    }

    private fun deductInventoryStock(): Boolean {
        val inventoryStr = inventoryPrefs.getString("PRODUCTS", "[]") ?: "[]"
        val inventoryArray = try {
            JSONArray(inventoryStr)
        } catch (e: Exception) {
            JSONArray()
        }

        // 1. Stock check
        for (i in 0 until cartItemsArray.length()) {
            val cartItem = cartItemsArray.getJSONObject(i)
            val cartName = cartItem.optString("name", "")
            val cartQty = cartItem.optInt("quantity", 1)

            for (j in 0 until inventoryArray.length()) {
                val invProduct = inventoryArray.getJSONObject(j)
                val invName = invProduct.optString("name", "")
                if (invName.equals(cartName, ignoreCase = true)) {
                    val availableStock = invProduct.optInt("stock", 0)
                    if (availableStock < cartQty) {
                        Toast.makeText(this, "Low stock for $cartName! Only $availableStock available.", Toast.LENGTH_LONG).show()
                        return false
                    }
                }
            }
        }

        // 2. Stock deduction
        for (i in 0 until cartItemsArray.length()) {
            val cartItem = cartItemsArray.getJSONObject(i)
            val cartName = cartItem.optString("name", "")
            val cartQty = cartItem.optInt("quantity", 1)

            for (j in 0 until inventoryArray.length()) {
                val invProduct = inventoryArray.getJSONObject(j)
                val invName = invProduct.optString("name", "")
                if (invName.equals(cartName, ignoreCase = true)) {
                    val currentStock = invProduct.optInt("stock", 0)
                    val newStock = (currentStock - cartQty).coerceAtLeast(0)
                    invProduct.put("stock", newStock)
                }
            }
        }

        inventoryPrefs.edit().putString("PRODUCTS", inventoryArray.toString()).apply()
        return true
    }

    private fun saveOrder(orderId: String, name: String, mobile: String, address: String, payment: String) {
        val ordersStr = orderPrefs.getString("ORDERS", "[]") ?: "[]"
        val ordersArray = try {
            JSONArray(ordersStr)
        } catch (e: Exception) {
            JSONArray()
        }

        val orderObj = JSONObject().apply {
            put("orderId", orderId)
            put("customerName", name)
            put("mobile", mobile)
            put("address", address)
            put("status", "Order Placed")
            put("items", cartItemsArray)
            put("payment", payment)
            put("total", totalAmount)
            put("timestamp", System.currentTimeMillis())
        }

        ordersArray.put(orderObj)
        orderPrefs.edit().putString("ORDERS", ordersArray.toString()).apply()
    }
}