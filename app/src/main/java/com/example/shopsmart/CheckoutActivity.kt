package com.example.shopsmart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject

class CheckoutActivity : Activity() {

    private lateinit var etName: EditText
    private lateinit var etMobile: EditText
    private lateinit var etAddress: EditText

    private lateinit var rgPaymentMethod: RadioGroup
    private lateinit var rbCod: RadioButton
    private lateinit var rbUpi: RadioButton
    private lateinit var rbCard: RadioButton

    private lateinit var etUpiId: EditText
    private lateinit var etCardNumber: EditText
    private lateinit var etCardExpiry: EditText
    private lateinit var etCardCvv: EditText

    private lateinit var layoutUpiDetails: LinearLayout
    private lateinit var layoutCardDetails: LinearLayout

    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: Button

    private var totalAmount = 0.0
    private var cartItemsArray = JSONArray()

    private val cartPrefs by lazy {
        getSharedPreferences("ShopSmartCart", Context.MODE_PRIVATE)
    }

    private val orderPrefs by lazy {
        getSharedPreferences("ShopSmartOrders", Context.MODE_PRIVATE)
    }

    private val inventoryPrefs by lazy {
        getSharedPreferences("ShopSmartInventory", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createCheckoutUI()
        loadCartData()
        setupPaymentListeners()
    }

    // ---------------------------------------------------------
    // CREATE UI PROGRAMMATICALLY
    // ---------------------------------------------------------

    private fun createCheckoutUI() {

        val scrollView = ScrollView(this)

        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setPadding(40, 40, 40, 40)

        scrollView.addView(mainLayout)

        // Title
        val title = TextView(this)
        title.text = "Checkout"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setPadding(0, 0, 0, 30)

        mainLayout.addView(title)

        // Name
        etName = createEditText(
            hint = "Enter your name",
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        )
        mainLayout.addView(etName)

        // Mobile
        etMobile = createEditText(
            hint = "Enter 10-digit mobile number",
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        )
        mainLayout.addView(etMobile)

        // Address
        etAddress = createEditText(
            hint = "Enter complete address",
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        )
        etAddress.minLines = 4
        mainLayout.addView(etAddress)

        // Payment title
        val paymentTitle = TextView(this)
        paymentTitle.text = "Payment Method"
        paymentTitle.textSize = 20f
        paymentTitle.setPadding(0, 30, 0, 10)
        mainLayout.addView(paymentTitle)

        // Radio Group
        rgPaymentMethod = RadioGroup(this)
        rgPaymentMethod.orientation = RadioGroup.VERTICAL

        rbCod = RadioButton(this)
        rbCod.text = "Cash on Delivery"
        rbCod.id = View.generateViewId()
        rbCod.isChecked = true

        rbUpi = RadioButton(this)
        rbUpi.text = "UPI"
        rbUpi.id = View.generateViewId()

        rbCard = RadioButton(this)
        rbCard.text = "Card"
        rbCard.id = View.generateViewId()

        rgPaymentMethod.addView(rbCod)
        rgPaymentMethod.addView(rbUpi)
        rgPaymentMethod.addView(rbCard)

        mainLayout.addView(rgPaymentMethod)

        // ---------------------------------------------------------
        // UPI DETAILS
        // ---------------------------------------------------------

        layoutUpiDetails = LinearLayout(this)
        layoutUpiDetails.orientation = LinearLayout.VERTICAL
        layoutUpiDetails.visibility = View.GONE

        etUpiId = createEditText(
            hint = "Enter UPI ID (example@upi)",
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        )

        layoutUpiDetails.addView(etUpiId)

        mainLayout.addView(layoutUpiDetails)

        // ---------------------------------------------------------
        // CARD DETAILS
        // ---------------------------------------------------------

        layoutCardDetails = LinearLayout(this)
        layoutCardDetails.orientation = LinearLayout.VERTICAL
        layoutCardDetails.visibility = View.GONE

        etCardNumber = createEditText(
            hint = "Card Number (16 digits)",
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        )

        etCardExpiry = createEditText(
            hint = "Expiry (MM/YY)",
            inputType = android.text.InputType.TYPE_CLASS_DATETIME
        )

        etCardCvv = createEditText(
            hint = "CVV",
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        )

        etCardCvv.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD

        layoutCardDetails.addView(etCardNumber)
        layoutCardDetails.addView(etCardExpiry)
        layoutCardDetails.addView(etCardCvv)

        mainLayout.addView(layoutCardDetails)

        // ---------------------------------------------------------
        // TOTAL
        // ---------------------------------------------------------

        tvTotalAmount = TextView(this)
        tvTotalAmount.text = "Total: ₹0.00"
        tvTotalAmount.textSize = 22f
        tvTotalAmount.setPadding(0, 30, 0, 20)

        mainLayout.addView(tvTotalAmount)

        // ---------------------------------------------------------
        // PLACE ORDER BUTTON
        // ---------------------------------------------------------

        btnPlaceOrder = Button(this)
        btnPlaceOrder.text = "Place Order"
        btnPlaceOrder.setOnClickListener {
            validateAndPlaceOrder()
        }

        mainLayout.addView(btnPlaceOrder)

        setContentView(scrollView)
    }

    private fun createEditText(
        hint: String,
        inputType: Int
    ): EditText {

        val editText = EditText(this)

        editText.hint = hint
        editText.inputType = inputType
        editText.setPadding(20, 20, 20, 20)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 10, 0, 10)

        editText.layoutParams = params

        return editText
    }

    // ---------------------------------------------------------
    // LOAD CART DATA
    // ---------------------------------------------------------

    private fun loadCartData() {

        val cartJsonString =
            cartPrefs.getString("CART", "[]") ?: "[]"

        cartItemsArray = try {
            JSONArray(cartJsonString)
        } catch (e: Exception) {
            JSONArray()
        }

        totalAmount = 0.0

        for (i in 0 until cartItemsArray.length()) {

            try {

                val item = cartItemsArray.getJSONObject(i)

                val price = item.optDouble("price", 0.0)

                val quantity = item.optInt("quantity", 1)

                totalAmount += price * quantity

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvTotalAmount.text =
            "Total: ₹${String.format("%.2f", totalAmount)}"
    }

    // ---------------------------------------------------------
    // PAYMENT LISTENER
    // ---------------------------------------------------------

    private fun setupPaymentListeners() {

        rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                rbCod.id -> {

                    layoutUpiDetails.visibility = View.GONE
                    layoutCardDetails.visibility = View.GONE
                }

                rbUpi.id -> {

                    layoutUpiDetails.visibility = View.VISIBLE
                    layoutCardDetails.visibility = View.GONE
                }

                rbCard.id -> {

                    layoutUpiDetails.visibility = View.GONE
                    layoutCardDetails.visibility = View.VISIBLE
                }
            }
        }
    }

    // ---------------------------------------------------------
    // VALIDATE AND PLACE ORDER
    // ---------------------------------------------------------

    private fun validateAndPlaceOrder() {

        val name = etName.text.toString().trim()

        val mobile = etMobile.text.toString().trim()

        val address = etAddress.text.toString().trim()

        // Name validation
        if (name.isEmpty()) {

            etName.error = "Please enter your name"

            etName.requestFocus()

            return
        }

        // Mobile validation
        if (!mobile.matches(Regex("^[0-9]{10}$"))) {

            etMobile.error =
                "Please enter valid 10-digit mobile number"

            etMobile.requestFocus()

            return
        }

        // Address validation
        if (address.isEmpty()) {

            etAddress.error =
                "Please enter complete address"

            etAddress.requestFocus()

            return
        }

        // Cart validation
        if (cartItemsArray.length() == 0) {

            Toast.makeText(
                this,
                "Your cart is empty!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // -----------------------------------------------------
        // PAYMENT METHOD
        // -----------------------------------------------------

        val paymentMethod: String

        when {

            rbCod.isChecked -> {

                paymentMethod = "Cash on Delivery"
            }

            rbUpi.isChecked -> {

                val upiId =
                    etUpiId.text.toString().trim()

                if (upiId.isEmpty() || !upiId.contains("@")) {

                    etUpiId.error =
                        "Enter valid UPI ID"

                    etUpiId.requestFocus()

                    return
                }

                paymentMethod = "UPI ($upiId)"
            }

            rbCard.isChecked -> {

                val cardNumber =
                    etCardNumber.text.toString().trim()

                val cardExpiry =
                    etCardExpiry.text.toString().trim()

                val cardCvv =
                    etCardCvv.text.toString().trim()

                // Card number
                if (!cardNumber.matches(Regex("^[0-9]{16}$"))) {

                    etCardNumber.error =
                        "Enter valid 16-digit card number"

                    etCardNumber.requestFocus()

                    return
                }

                // Expiry
                if (!cardExpiry.matches(
                        Regex("^(0[1-9]|1[0-2])/([0-9]{2})$")
                    )
                ) {

                    etCardExpiry.error =
                        "Enter expiry as MM/YY"

                    etCardExpiry.requestFocus()

                    return
                }

                // CVV
                if (!cardCvv.matches(Regex("^[0-9]{3}$"))) {

                    etCardCvv.error =
                        "Enter valid 3-digit CVV"

                    etCardCvv.requestFocus()

                    return
                }

                val lastFour =
                    cardNumber.takeLast(4)

                paymentMethod =
                    "Card (Ending with $lastFour)"
            }

            else -> {

                paymentMethod = "Cash on Delivery"
            }
        }

        // -----------------------------------------------------
        // DEDUCT INVENTORY
        // -----------------------------------------------------

        if (!deductInventoryStock()) {
            return
        }

        // -----------------------------------------------------
        // GENERATE ORDER ID
        // -----------------------------------------------------

        val orderId =
            "ORD${System.currentTimeMillis() % 1000000}"

        // -----------------------------------------------------
        // SAVE ORDER
        // -----------------------------------------------------

        saveOrder(
            orderId = orderId,
            name = name,
            mobile = mobile,
            address = address,
            payment = paymentMethod
        )

        // -----------------------------------------------------
        // CLEAR CART
        // -----------------------------------------------------

        cartPrefs.edit()
            .remove("CART")
            .apply()

        // -----------------------------------------------------
        // SUCCESS MESSAGE
        // -----------------------------------------------------

        AlertDialogHelper.showOrderSuccess(
            this,
            orderId
        )
    }

    // ---------------------------------------------------------
    // INVENTORY
    // ---------------------------------------------------------

    private fun deductInventoryStock(): Boolean {

        val inventoryString =
            inventoryPrefs.getString("PRODUCTS", "[]")
                ?: "[]"

        val inventoryArray = try {

            JSONArray(inventoryString)

        } catch (e: Exception) {

            JSONArray()
        }

        // -----------------------------------------------------
        // CHECK STOCK FIRST
        // -----------------------------------------------------

        for (i in 0 until cartItemsArray.length()) {

            val cartItem =
                cartItemsArray.getJSONObject(i)

            val cartName =
                cartItem.optString("name", "")

            val cartQuantity =
                cartItem.optInt("quantity", 1)

            var productFound = false

            for (j in 0 until inventoryArray.length()) {

                val inventoryProduct =
                    inventoryArray.getJSONObject(j)

                val inventoryName =
                    inventoryProduct.optString("name", "")

                if (
                    inventoryName.equals(
                        cartName,
                        ignoreCase = true
                    )
                ) {

                    productFound = true

                    val availableStock =
                        inventoryProduct.optInt(
                            "stock",
                            0
                        )

                    if (availableStock < cartQuantity) {

                        Toast.makeText(
                            this,
                            "Low stock for $cartName! Only $availableStock available.",
                            Toast.LENGTH_LONG
                        ).show()

                        return false
                    }

                    break
                }
            }

            if (!productFound) {

                Toast.makeText(
                    this,
                    "Product not found in inventory: $cartName",
                    Toast.LENGTH_LONG
                ).show()

                return false
            }
        }

        // -----------------------------------------------------
        // DEDUCT STOCK
        // -----------------------------------------------------

        for (i in 0 until cartItemsArray.length()) {

            val cartItem =
                cartItemsArray.getJSONObject(i)

            val cartName =
                cartItem.optString("name", "")

            val cartQuantity =
                cartItem.optInt("quantity", 1)

            for (j in 0 until inventoryArray.length()) {

                val inventoryProduct =
                    inventoryArray.getJSONObject(j)

                val inventoryName =
                    inventoryProduct.optString("name", "")

                if (
                    inventoryName.equals(
                        cartName,
                        ignoreCase = true
                    )
                ) {

                    val currentStock =
                        inventoryProduct.optInt(
                            "stock",
                            0
                        )

                    val newStock =
                        (currentStock - cartQuantity)
                            .coerceAtLeast(0)

                    inventoryProduct.put(
                        "stock",
                        newStock
                    )

                    break
                }
            }
        }

        // -----------------------------------------------------
        // SAVE INVENTORY
        // -----------------------------------------------------

        inventoryPrefs.edit()
            .putString(
                "PRODUCTS",
                inventoryArray.toString()
            )
            .apply()

        return true
    }

    // ---------------------------------------------------------
    // SAVE ORDER
    // ---------------------------------------------------------

    private fun saveOrder(
        orderId: String,
        name: String,
        mobile: String,
        address: String,
        payment: String
    ) {

        val ordersString =
            orderPrefs.getString(
                "ORDERS",
                "[]"
            ) ?: "[]"

        val ordersArray = try {

            JSONArray(ordersString)

        } catch (e: Exception) {

            JSONArray()
        }

        val orderObject =
            JSONObject()

        orderObject.put(
            "orderId",
            orderId
        )

        orderObject.put(
            "customerName",
            name
        )

        orderObject.put(
            "mobile",
            mobile
        )

        orderObject.put(
            "address",
            address
        )

        orderObject.put(
            "status",
            "Order Placed"
        )

        orderObject.put(
            "items",
            cartItemsArray
        )

        orderObject.put(
            "payment",
            payment
        )

        orderObject.put(
            "total",
            totalAmount
        )

        orderObject.put(
            "timestamp",
            System.currentTimeMillis()
        )

        ordersArray.put(orderObject)

        orderPrefs.edit()
            .putString(
                "ORDERS",
                ordersArray.toString()
            )
            .apply()
    }
}

// =============================================================
// SIMPLE ORDER SUCCESS DIALOG
// =============================================================

object AlertDialogHelper {

    fun showOrderSuccess(
        activity: Activity,
        orderId: String
    ) {

        android.app.AlertDialog.Builder(activity)
            .setTitle("Order Placed Successfully")
            .setMessage(
                "Your order has been placed.\n\nOrder ID: $orderId"
            )
            .setPositiveButton("OK") { dialog, _ ->

                dialog.dismiss()
                activity.finish()
            }
            .setCancelable(false)
            .show()
    }
}