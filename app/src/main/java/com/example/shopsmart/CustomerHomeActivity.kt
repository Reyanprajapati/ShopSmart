package com.example.shopsmart

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt

class CustomerHomeActivity : Activity() {

    private lateinit var tvWelcomeUser: TextView
    private lateinit var etHomeSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createHomeUI()
        loadUserData()
    }

    // =========================================================
    // CREATE HOME SCREEN
    // =========================================================

    private fun createHomeUI() {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.WHITE)

        // =====================================================
        // TOP BAR
        // =====================================================

        val topBar = LinearLayout(this)

        topBar.orientation = LinearLayout.HORIZONTAL
        topBar.gravity = Gravity.CENTER_VERTICAL
        topBar.setPadding(20, 20, 20, 20)

        tvWelcomeUser = TextView(this)

        tvWelcomeUser.setText(R.string.welcome_customer)
        tvWelcomeUser.textSize = 20f
        tvWelcomeUser.setTextColor(Color.BLACK)
        tvWelcomeUser.setTypeface(null, Typeface.BOLD)

        topBar.addView(
            tvWelcomeUser,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        // Notifications
        val btnNotifications = Button(this)
        btnNotifications.text = "🔔"
        btnNotifications.textSize = 18f

        btnNotifications.setOnClickListener {

            try {

                startActivity(
                    Intent(
                        this,
                        NotificationsActivity::class.java
                    )
                )

            } catch (_: Exception) {

                Toast.makeText(
                    this,
                    "Notifications screen not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        topBar.addView(
            btnNotifications,
            LinearLayout.LayoutParams(
                60,
                60
            )
        )

        // Cart
        val btnCart = Button(this)
        btnCart.text = "🛒"
        btnCart.textSize = 18f

        btnCart.setOnClickListener {

            try {

                startActivity(
                    Intent(
                        this,
                        CartActivity::class.java
                    )
                )

            } catch (_: Exception) {

                Toast.makeText(
                    this,
                    "Cart screen not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        topBar.addView(
            btnCart,
            LinearLayout.LayoutParams(
                60,
                60
            )
        )

        // Profile
        val btnProfile = Button(this)
        btnProfile.text = "👤"
        btnProfile.textSize = 18f

        btnProfile.setOnClickListener {

            try {

                startActivity(
                    Intent(
                        this,
                        ProfileActivity::class.java
                    )
                )

            } catch (_: Exception) {

                Toast.makeText(
                    this,
                    "Profile screen not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        topBar.addView(
            btnProfile,
            LinearLayout.LayoutParams(
                60,
                60
            )
        )

        root.addView(topBar)

        // =====================================================
        // SCROLL VIEW
        // =====================================================

        val scrollView = ScrollView(this)

        val content = LinearLayout(this)

        content.orientation = LinearLayout.VERTICAL
        content.setPadding(20, 10, 20, 30)

        scrollView.addView(content)

        // =====================================================
        // SEARCH BAR
        // =====================================================

        etHomeSearch = EditText(this)

        etHomeSearch.setHint(R.string.search_products_hint)
        etHomeSearch.textSize = 16f
        etHomeSearch.setSingleLine(true)

        etHomeSearch.imeOptions =
            EditorInfo.IME_ACTION_SEARCH

        content.addView(
            etHomeSearch,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        etHomeSearch.setOnEditorActionListener {
                _, actionId, _ ->

            if (
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {

                openProducts(
                    "",
                    etHomeSearch.text.toString().trim()
                )

                true

            } else {

                false
            }
        }

        // =====================================================
        // BROWSE ALL BUTTON
        // =====================================================

        val btnBrowseAll = Button(this)

        btnBrowseAll.setText(R.string.browse_all_products)
        btnBrowseAll.textSize = 16f

        val browseParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        browseParams.topMargin = 20

        content.addView(
            btnBrowseAll,
            browseParams
        )

        btnBrowseAll.setOnClickListener {

            openProducts(
                "All",
                ""
            )
        }

        // =====================================================
        // CATEGORY TITLE
        // =====================================================

        val categoryTitle = TextView(this)

        categoryTitle.setText(R.string.shop_by_category)
        categoryTitle.textSize = 22f
        categoryTitle.setTextColor(Color.BLACK)
        categoryTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        val categoryTitleParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        categoryTitleParams.topMargin = 25

        content.addView(
            categoryTitle,
            categoryTitleParams
        )

        // =====================================================
        // CATEGORY CARDS
        // =====================================================

        addCategoryCard(
            content,
            "🍚 Grocery",
            "Rice, flour, pulses and more",
            "Grocery"
        )

        addCategoryCard(
            content,
            "🥛 Dairy",
            "Milk, cheese, butter and more",
            "Dairy"
        )

        addCategoryCard(
            content,
            "🍿 Snacks",
            "Chips, biscuits and snacks",
            "Snacks"
        )

        addCategoryCard(
            content,
            "🥤 Drinks",
            "Juices, soft drinks and beverages",
            "Drinks"
        )

        // =====================================================
        // MY ORDERS
        // =====================================================

        val ordersCard = LinearLayout(this)

        ordersCard.orientation =
            LinearLayout.VERTICAL

        ordersCard.setPadding(
            20,
            25,
            20,
            25
        )

        ordersCard.setBackgroundColor(
            "#F3F3F3".toColorInt()
        )

        val ordersParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        ordersParams.topMargin = 25

        ordersCard.layoutParams = ordersParams

        val ordersTitle = TextView(this)

        ordersTitle.setText(R.string.my_orders)
        ordersTitle.textSize = 20f
        ordersTitle.setTextColor(Color.BLACK)
        ordersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        ordersCard.addView(ordersTitle)

        val ordersSubtitle = TextView(this)

        ordersSubtitle.setText(R.string.my_orders_subtitle)
        ordersSubtitle.textSize = 14f
        ordersSubtitle.setTextColor(
            Color.DKGRAY
        )

        ordersCard.addView(
            ordersSubtitle
        )

        ordersCard.setOnClickListener {

            try {

                startActivity(
                    Intent(
                        this,
                        OrdersActivity::class.java
                    )
                )

            } catch (_: Exception) {

                Toast.makeText(
                    this,
                    "Orders screen not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        content.addView(ordersCard)

        // Add scroll view
        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    // =========================================================
    // CATEGORY CARD
    // =========================================================

    private fun addCategoryCard(
        parent: LinearLayout,
        title: String,
        description: String,
        category: String
    ) {

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
            "#F5F5F5".toColorInt()
        )

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.topMargin = 12

        card.layoutParams = params

        val titleView = TextView(this)

        titleView.text = title
        titleView.textSize = 20f
        titleView.setTextColor(Color.BLACK)
        titleView.setTypeface(
            null,
            Typeface.BOLD
        )

        card.addView(titleView)

        val descriptionView = TextView(this)

        descriptionView.text = description
        descriptionView.textSize = 14f
        descriptionView.setTextColor(Color.DKGRAY)

        card.addView(
            descriptionView
        )

        card.setOnClickListener {

            openProducts(
                category,
                ""
            )
        }

        parent.addView(card)
    }

    // =========================================================
    // LOAD USER DATA
    // =========================================================

    private fun loadUserData() {

        val prefs =
            getSharedPreferences(
                "ShopSmartPrefs",
                MODE_PRIVATE
            )

        val username =
            prefs.getString(
                "USERNAME",
                "Customer"
            ) ?: "Customer"

        tvWelcomeUser.text =
            getString(R.string.welcome_user, username)
    }

    // =========================================================
    // OPEN PRODUCTS
    // =========================================================

    private fun openProducts(
        category: String,
        search: String
    ) {

        try {

            val intent = Intent(
                this,
                CustomerProductsActivity::class.java
            )

            intent.putExtra(
                "SELECTED_CATEGORY",
                category
            )

            intent.putExtra(
                "SEARCH_QUERY",
                search
            )

            startActivity(intent)

        } catch (_: Exception) {

            Toast.makeText(
                this,
                "Products screen not found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}