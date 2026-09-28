package com.example.shopsmart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : Activity() {

    private lateinit var layoutCartItems: LinearLayout
    private lateinit var tvEmptyCart: TextView
    private lateinit var layoutBottomCheckout: LinearLayout
    private lateinit var tvCartTotal: TextView
    private lateinit var btnCheckout: Button

    private lateinit var cartPrefs: android.content.SharedPreferences

    private var cartArray = JSONArray()
    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPrefs()
        createCartUI()
        loadCart()
    }

    // =========================================================
    // SHARED PREFERENCES
    // =========================================================

    private fun initPrefs() {
        cartPrefs = getSharedPreferences(
            "ShopSmartCart",
            Context.MODE_PRIVATE
        )
    }

    // =========================================================
    // CREATE CART UI
    // =========================================================

    private fun createCartUI() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.WHITE)

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(20, 20, 20, 20)
        header.setBackgroundColor(Color.WHITE)

        val btnBack = TextView(this)
        btnBack.text = "←"
        btnBack.textSize = 30f
        btnBack.setTextColor(Color.BLACK)
        btnBack.gravity = Gravity.CENTER
        btnBack.setPadding(10, 0, 20, 0)

        btnBack.setOnClickListener {
            finish()
        }

        val tvTitle = TextView(this)
        tvTitle.text = "My Cart"
        tvTitle.textSize = 24f
        tvTitle.setTextColor(Color.BLACK)
        tvTitle.setTypeface(null, Typeface.BOLD)
        tvTitle.gravity = Gravity.CENTER_VERTICAL

        header.addView(
            btnBack,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        header.addView(
            tvTitle,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(header)

        // -----------------------------------------------------
        // SCROLL VIEW
        // -----------------------------------------------------

        val scrollView = ScrollView(this)

        layoutCartItems = LinearLayout(this)
        layoutCartItems.orientation = LinearLayout.VERTICAL
        layoutCartItems.setPadding(20, 10, 20, 20)

        scrollView.addView(layoutCartItems)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // -----------------------------------------------------
        // EMPTY CART MESSAGE
        // -----------------------------------------------------

        tvEmptyCart = TextView(this)
        tvEmptyCart.text = "Your cart is empty 🛒"
        tvEmptyCart.textSize = 20f
        tvEmptyCart.setTextColor(Color.DKGRAY)
        tvEmptyCart.gravity = Gravity.CENTER
        tvEmptyCart.setPadding(20, 80, 20, 80)
        tvEmptyCart.visibility = View.GONE

        layoutCartItems.addView(tvEmptyCart)

        // -----------------------------------------------------
        // BOTTOM CHECKOUT SECTION
        // -----------------------------------------------------

        layoutBottomCheckout = LinearLayout(this)
        layoutBottomCheckout.orientation = LinearLayout.VERTICAL
        layoutBottomCheckout.setPadding(20, 15, 20, 20)
        layoutBottomCheckout.setBackgroundColor(
            Color.parseColor("#F5F5F5")
        )

        val totalRow = LinearLayout(this)
        totalRow.orientation = LinearLayout.HORIZONTAL
        totalRow.gravity = Gravity.CENTER_VERTICAL

        val tvTotalLabel = TextView(this)
        tvTotalLabel.text = "Total Amount"
        tvTotalLabel.textSize = 18f
        tvTotalLabel.setTextColor(Color.BLACK)
        tvTotalLabel.setTypeface(null, Typeface.BOLD)

        tvCartTotal = TextView(this)
        tvCartTotal.text = "₹0.00"
        tvCartTotal.textSize = 20f
        tvCartTotal.setTextColor(
            Color.parseColor("#6C63FF")
        )
        tvCartTotal.setTypeface(null, Typeface.BOLD)
        tvCartTotal.gravity = Gravity.END

        totalRow.addView(
            tvTotalLabel,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        totalRow.addView(
            tvCartTotal,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        layoutBottomCheckout.addView(totalRow)

        // -----------------------------------------------------
        // CHECKOUT BUTTON
        // -----------------------------------------------------

        btnCheckout = Button(this)
        btnCheckout.text = "Proceed to Checkout"
        btnCheckout.textSize = 16f
        btnCheckout.setTextColor(Color.WHITE)
        btnCheckout.setBackgroundColor(
            Color.parseColor("#6C63FF")
        )

        val checkoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        checkoutParams.topMargin = 15

        layoutBottomCheckout.addView(
            btnCheckout,
            checkoutParams
        )

        btnCheckout.setOnClickListener {

            if (cartArray.length() == 0) {

                Toast.makeText(
                    this,
                    "Cart is empty!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val intent = Intent(
                    this,
                    CheckoutActivity::class.java
                )

                startActivity(intent)
            }
        }

        root.addView(
            layoutBottomCheckout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }

    // =========================================================
    // LOAD CART
    // =========================================================

    private fun loadCart() {

        layoutCartItems.removeAllViews()

        // Re-add empty message after removeAllViews()
        layoutCartItems.addView(tvEmptyCart)

        val cartString =
            cartPrefs.getString(
                "CART",
                "[]"
            ) ?: "[]"

        cartArray = try {

            JSONArray(cartString)

        } catch (e: Exception) {

            JSONArray()
        }

        totalAmount = 0.0

        // -----------------------------------------------------
        // EMPTY CART
        // -----------------------------------------------------

        if (cartArray.length() == 0) {

            tvEmptyCart.visibility = View.VISIBLE
            layoutBottomCheckout.visibility = View.GONE

            return
        }

        // -----------------------------------------------------
        // CART HAS ITEMS
        // -----------------------------------------------------

        tvEmptyCart.visibility = View.GONE
        layoutBottomCheckout.visibility = View.VISIBLE

        for (i in 0 until cartArray.length()) {

            try {

                val item =
                    cartArray.getJSONObject(i)

                val card =
                    createCartItemCard(
                        item,
                        i
                    )

                layoutCartItems.addView(card)

                val price =
                    item.optDouble(
                        "price",
                        0.0
                    )

                val quantity =
                    item.optInt(
                        "quantity",
                        1
                    )

                totalAmount +=
                    price * quantity

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }

        tvCartTotal.text =
            "₹${String.format("%.2f", totalAmount)}"
    }

    // =========================================================
    // CREATE CART ITEM
    // =========================================================

    private fun createCartItemCard(
        item: JSONObject,
        index: Int
    ): View {

        val name =
            item.optString(
                "name",
                "Product"
            )

        val price =
            item.optDouble(
                "price",
                0.0
            )

        val quantity =
            item.optInt(
                "quantity",
                1
            )

        val emoji =
            item.optString(
                "emoji",
                "🛍️"
            )

        // -----------------------------------------------------
        // CARD
        // -----------------------------------------------------

        val card = LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            20,
            20,
            20,
            20
        )

        card.setBackgroundColor(
            Color.parseColor("#F8F8F8")
        )

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            0,
            0,
            15
        )

        card.layoutParams = cardParams

        // -----------------------------------------------------
        // TOP ROW
        // -----------------------------------------------------

        val topRow = LinearLayout(this)

        topRow.orientation =
            LinearLayout.HORIZONTAL

        topRow.gravity =
            Gravity.CENTER_VERTICAL

        // Emoji
        val tvEmoji =
            TextView(this)

        tvEmoji.text = emoji
        tvEmoji.textSize = 35f
        tvEmoji.setPadding(
            0,
            0,
            15,
            0
        )

        // Details
        val details =
            LinearLayout(this)

        details.orientation =
            LinearLayout.VERTICAL

        details.layoutParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        val tvName =
            TextView(this)

        tvName.text = name
        tvName.textSize = 17f
        tvName.setTextColor(
            Color.parseColor("#202124")
        )
        tvName.setTypeface(
            null,
            Typeface.BOLD
        )

        val tvPrice =
            TextView(this)

        tvPrice.text =
            "₹${String.format("%.2f", price)} × $quantity = ₹${
                String.format("%.2f", price * quantity)
            }"

        tvPrice.textSize = 14f
        tvPrice.setTextColor(
            Color.parseColor("#6C63FF")
        )

        details.addView(tvName)
        details.addView(tvPrice)

        topRow.addView(tvEmoji)
        topRow.addView(details)

        card.addView(topRow)

        // -----------------------------------------------------
        // QUANTITY ROW
        // -----------------------------------------------------

        val quantityRow =
            LinearLayout(this)

        quantityRow.orientation =
            LinearLayout.HORIZONTAL

        quantityRow.gravity =
            Gravity.CENTER_VERTICAL

        quantityRow.setPadding(
            0,
            15,
            0,
            0
        )

        val minusButton =
            Button(this)

        minusButton.text = "−"
        minusButton.textSize = 18f

        minusButton.setOnClickListener {

            updateQuantity(
                index,
                -1
            )
        }

        val qtyText =
            TextView(this)

        qtyText.text =
            "  $quantity  "

        qtyText.textSize = 18f
        qtyText.gravity =
            Gravity.CENTER

        val plusButton =
            Button(this)

        plusButton.text = "+"
        plusButton.textSize = 18f

        plusButton.setOnClickListener {

            updateQuantity(
                index,
                1
            )
        }

        val deleteButton =
            Button(this)

        deleteButton.text = "🗑️"
        deleteButton.textSize = 16f

        deleteButton.setOnClickListener {

            deleteItem(index)
        }

        quantityRow.addView(
            minusButton,
            LinearLayout.LayoutParams(
                60,
                55
            )
        )

        quantityRow.addView(
            qtyText,
            LinearLayout.LayoutParams(
                70,
                55
            )
        )

        quantityRow.addView(
            plusButton,
            LinearLayout.LayoutParams(
                60,
                55
            )
        )

        val deleteParams =
            LinearLayout.LayoutParams(
                60,
                55
            )

        deleteParams.leftMargin = 20

        quantityRow.addView(
            deleteButton,
            deleteParams
        )

        card.addView(quantityRow)

        return card
    }

    // =========================================================
    // UPDATE QUANTITY
    // =========================================================

    private fun updateQuantity(
        index: Int,
        change: Int
    ) {

        if (
            index < 0 ||
            index >= cartArray.length()
        ) {
            return
        }

        try {

            val item =
                cartArray.getJSONObject(index)

            val currentQuantity =
                item.optInt(
                    "quantity",
                    1
                )

            val newQuantity =
                currentQuantity + change

            if (newQuantity <= 0) {

                cartArray.remove(index)

            } else {

                item.put(
                    "quantity",
                    newQuantity
                )
            }

            cartPrefs.edit()
                .putString(
                    "CART",
                    cartArray.toString()
                )
                .apply()

            loadCart()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to update cart",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // DELETE ITEM
    // =========================================================

    private fun deleteItem(index: Int) {

        if (
            index < 0 ||
            index >= cartArray.length()
        ) {
            return
        }

        cartArray.remove(index)

        cartPrefs.edit()
            .putString(
                "CART",
                cartArray.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Item removed from cart",
            Toast.LENGTH_SHORT
        ).show()

        loadCart()
    }

    // =========================================================
    // REFRESH WHEN RETURNING TO CART
    // =========================================================

    override fun onResume() {
        super.onResume()

        if (::layoutCartItems.isInitialized) {
            loadCart()
        }
    }
}