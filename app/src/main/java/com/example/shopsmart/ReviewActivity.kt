package com.example.shopsmart

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONArray
import org.json.JSONObject

class ReviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_review)

        val orderId =
            intent.getStringExtra("ORDER_ID") ?: ""

        val rating =
            findViewById<RatingBar>(
                R.id.ratingBar
            )

        val review =
            findViewById<EditText>(
                R.id.etReview
            )

        val submit =
            findViewById<Button>(
                R.id.btnSubmitReview
            )

        val back =
            findViewById<Button>(
                R.id.btnReviewBack
            )

        back.setOnClickListener {
            finish()
        }

        submit.setOnClickListener {

            val ratingValue =
                rating.rating

            val reviewText =
                review.text.toString().trim()

            if (ratingValue == 0f) {
                Toast.makeText(
                    this,
                    "Please select rating",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (reviewText.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please write a review",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val preferences =
                getSharedPreferences(
                    "ShopSmartReviews",
                    MODE_PRIVATE
                )

            val oldData =
                preferences.getString(
                    "REVIEWS",
                    "[]"
                ) ?: "[]"

            val reviews =
                try {
                    JSONArray(oldData)
                } catch (e: Exception) {
                    JSONArray()
                }

            val username =
                getSharedPreferences(
                    "ShopSmartPrefs",
                    MODE_PRIVATE
                ).getString(
                    "USERNAME",
                    "Customer"
                ) ?: "Customer"

            val objectReview =
                JSONObject()

            objectReview.put(
                "orderId",
                orderId
            )

            objectReview.put(
                "username",
                username
            )

            objectReview.put(
                "rating",
                ratingValue
            )

            objectReview.put(
                "review",
                reviewText
            )

            reviews.put(objectReview)

            preferences.edit()
                .putString(
                    "REVIEWS",
                    reviews.toString()
                )
                .apply()

            Toast.makeText(
                this,
                "Review submitted ⭐",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}