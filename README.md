# 🛍️ ShopSmart -- Smart Retail & Inventory Management App

ShopSmart is an Android application developed in **Kotlin** for managing
a small retail shop and providing a customer shopping experience.

The project contains two main roles:

-   👨‍💼 **Seller / Shop Owner**
-   🛒 **Customer**

The app uses Android XML layouts and Kotlin activities. Local data is
stored using `SharedPreferences` and JSON (`JSONArray`).

------------------------------------------------------------------------

## ✨ Main Features

### 👨‍💼 Seller Features

-   🔐 Seller Login
-   🏠 Seller Dashboard
-   📦 Inventory Management
-   ➕ Add Products
-   🔍 Search Products
-   📊 Product Count
-   🛒 Order Count
-   📋 Customer Orders
-   🔔 Seller Notifications
-   👤 Seller Profile
-   🧭 Bottom Navigation
-   📈 Quick Inventory / Dashboard statistics

### 🛒 Customer Features

-   🔐 Customer Login / Signup
-   🏠 Customer Home
-   🔍 Product Search
-   🗂️ Product Categories
-   🛍️ Browse All Products
-   ➕ Add to Cart
-   🛒 Cart Management
-   ➕ Increase Quantity
-   ➖ Decrease Quantity
-   🗑️ Remove Items
-   💰 Cart Total
-   💳 Checkout
-   📦 Place Order
-   📜 Order History
-   ❤️ Wishlist
-   🔔 Notifications
-   ⭐ Product Reviews / Ratings
-   🧾 Order Invoice
-   👤 Customer Profile
-   🌙 Dark Mode
-   🧭 Navigation between screens

------------------------------------------------------------------------

## 🧰 Technologies Used

  Technology               Purpose
  ------------------------ ----------------------------------
  Kotlin                   Application programming language
  Android Studio           Development environment
  XML                      UI layouts
  Android SDK              Android application framework
  SharedPreferences        Local data storage
  JSONArray / JSONObject   Local JSON-based data
  Intent                   Activity navigation
  Toast                    User feedback messages

------------------------------------------------------------------------

## 📁 Project Structure

``` text
ShopSmart/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/shopsmart/
│           │
│           │   ├── MainActivity.kt
│           │   ├── SignupActivity.kt
│           │   ├── HomeActivity.kt
│           │   ├── InventoryActivity.kt
│           │   ├── ProfileActivity.kt
│           │   │
│           │   ├── CustomerHomeActivity.kt
│           │   ├── CustomerProductsActivity.kt
│           │   ├── CartActivity.kt
│           │   ├── CheckoutActivity.kt
│           │   ├── OrderActivity.kt
│           │   ├── OrderHistoryActivity.kt
│           │   ├── WishlistActivity.kt
│           │   ├── NotificationsActivity.kt
│           │   ├── ReviewsActivity.kt
│           │   └── SellerOrdersActivity.kt
│           │
│           ├── res/
│           │   ├── layout/
│           │   ├── drawable/
│           │   ├── values/
│           │   └── mipmap/
│           │
│           └── AndroidManifest.xml
│
└── README.md
```

> The exact list of activities can vary depending on the final version
> of the project.

------------------------------------------------------------------------

## 🔐 Login & User Flow

### Seller Flow

``` text
MainActivity
     ↓
Seller Login
     ↓
HomeActivity
     ├── Inventory
     ├── Customer Orders
     ├── Notifications
     └── Profile
```

### Customer Flow

``` text
Customer Login / Signup
          ↓
Customer Home
          ↓
Browse Products
          ↓
Product Details
          ↓
Add to Cart
          ↓
Cart
          ↓
Checkout
          ↓
Place Order
          ↓
Order Confirmation
          ↓
Order History
```

------------------------------------------------------------------------

## 🛒 Shopping Flow

1.  Customer logs in.
2.  Customer opens the product section.
3.  Customer searches or selects a category.
4.  Customer selects a product.
5.  Customer adds the product to the cart.
6.  Customer opens **My Cart**.
7.  Customer changes quantity using `+` and `−`.
8.  Cart total is automatically calculated.
9.  Customer proceeds to checkout.
10. Customer confirms the order.
11. Order information is saved.
12. Customer can view the order in **Order History**.
13. Seller can view customer orders.

------------------------------------------------------------------------

## 📦 Inventory Flow

Seller can manage products through the inventory section.

Typical product information includes:

-   Product name
-   Category
-   Price
-   Quantity / stock
-   Emoji or product icon
-   Availability status

Example products:

-   🍚 Rice
-   🌾 Wheat
-   🥛 Milk
-   🍪 Biscuits
-   🍬 Sugar
-   🫗 Cooking Oil
-   🧼 Soap
-   🍵 Tea
-   🧴 Shampoo
-   🥤 Cold Drink

------------------------------------------------------------------------

## 🗂️ Product Categories

The application can organize products into categories such as:

-   🛒 Grocery
-   🥛 Dairy
-   🧴 Personal Care
-   🍪 Snacks
-   🥤 Drinks
-   🏠 Household

Categories can be expanded by adding more products and category filters.

------------------------------------------------------------------------

## 💾 Local Data Storage

ShopSmart currently uses Android `SharedPreferences` for simple local
storage.

Example storage areas:

``` text
ShopSmartPrefs
ShopSmartCart
ShopSmartInventory
ShopSmartOrders
ShopSmartWishlist
ShopSmartNotifications
```

JSON is used to store lists of products, cart items, and orders.

Example:

``` json
[
  {
    "name": "Milk",
    "price": 60,
    "quantity": 2
  }
]
```

------------------------------------------------------------------------

## 🧾 Order Management

After checkout, an order can contain:

-   Order ID
-   Customer name
-   Products
-   Quantity
-   Total amount
-   Order date
-   Order status

Example status flow:

``` text
Pending
   ↓
Confirmed
   ↓
Packed
   ↓
Shipped
   ↓
Delivered
```

The seller can use the order section to monitor customer orders.

------------------------------------------------------------------------

## ❤️ Wishlist

Customers can save products for later using the Wishlist feature.

``` text
Product
  ↓
❤️ Add to Wishlist
  ↓
Wishlist
  ↓
Add to Cart
```

------------------------------------------------------------------------

## ⭐ Reviews & Ratings

Customers can provide feedback after purchasing a product.

Example:

``` text
Product: Milk

⭐⭐⭐⭐⭐

"Good quality and fresh."
```

Reviews can be connected to the product and displayed on the product
details screen.

------------------------------------------------------------------------

## 🔔 Notifications

Notifications can be used for:

-   🎉 Order placed
-   📦 Order confirmed
-   🚚 Order shipped
-   ✅ Order delivered
-   🏷️ New products
-   ❤️ Wishlist updates
-   ⚠️ Low stock notifications for seller

------------------------------------------------------------------------

## 🧾 Invoice

After an order is placed, the application can display an invoice
containing:

``` text
ShopSmart
-------------------------
Order ID
Customer Name
Date

Product       Qty   Price
Milk           2    ₹120
Rice           1    ₹60
-------------------------
Total              ₹180
-------------------------
Thank you for shopping!
```

------------------------------------------------------------------------

## 🌙 Dark Mode

Dark Mode changes the application's appearance for better usability in
low-light environments.

The implementation can use:

-   SharedPreferences for saving the user's preference
-   Android theme resources
-   Separate light/dark color resources

Example preference:

``` text
Dark Mode = ON
```

The preference should remain active when the application is reopened.

------------------------------------------------------------------------

## 🎨 UI Design

The application uses:

-   Gradient headers
-   Rounded cards
-   Product cards
-   Bottom navigation
-   Search bars
-   Buttons
-   Emoji-based product icons
-   Consistent purple/blue theme
-   Light gray background
-   Card elevation/shadows

The UI is designed to be simple and suitable for a small retail
application.

------------------------------------------------------------------------

## 🧭 Navigation

Main seller navigation:

``` text
🏠 Home
📦 Inventory
👤 Profile
```

Customer navigation can include:

``` text
🏠 Home
🛍️ Products
🛒 Cart
❤️ Wishlist
👤 Profile
```

Additional screens are opened using Android `Intent`.

------------------------------------------------------------------------

## ⚙️ Installation & Setup

### Requirements

-   Android Studio
-   Android SDK
-   Kotlin support
-   Android device or emulator

### Steps

1.  Open **Android Studio**.
2.  Select **Open**.
3.  Select the `ShopSmart` project folder.
4.  Allow Gradle to sync.
5.  Check that the package name is:

``` text
com.example.shopsmart
```

6.  Check `AndroidManifest.xml`.
7.  Make sure all activities used by the application are declared.
8.  Select an emulator or connect an Android phone.
9.  Click **Run ▶**.

------------------------------------------------------------------------

## 🐛 Common Problems

### 1. `Redeclaration: SignupActivity`

This usually means that `SignupActivity` has been declared twice.

Keep only one:

``` kotlin
class SignupActivity : ComponentActivity()
```

------------------------------------------------------------------------

### 2. `Unresolved reference`

Check:

-   Activity name
-   XML ID
-   Layout filename
-   Package name
-   Imports

For example:

``` kotlin
findViewById<TextView>(R.id.tvWelcome)
```

requires an XML view with:

``` xml
android:id="@+id/tvWelcome"
```

------------------------------------------------------------------------

### 3. `Function invocation 'length()' expected`

For Kotlin `JSONArray`, use:

``` kotlin
cart.length()
```

not:

``` kotlin
cart.length
```

For Kotlin collections such as `List`, use:

``` kotlin
list.size
```

------------------------------------------------------------------------

### 4. Android Manifest namespace error

The manifest must contain:

``` xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
```

------------------------------------------------------------------------

### 5. Button not responding

Check that:

-   The button has the correct ID.
-   `findViewById()` uses the same ID.
-   `setOnClickListener` is inside `onCreate()`.
-   The correct layout is loaded with `setContentView()`.

------------------------------------------------------------------------

## 🔒 Data & Security Note

This project is designed as a student/demo Android application using
local storage.

For a production e-commerce application, authentication and order data
should be moved to a secure backend/database such as Firebase or another
server-side system. Passwords should not be stored as plain text.

------------------------------------------------------------------------

## 🚀 Future Improvements

Possible future improvements include:

-   ☁️ Firebase Authentication
-   ☁️ Firebase Firestore / Realtime Database
-   🖼️ Real product images
-   💳 Online payment integration
-   📍 Delivery address management
-   🚚 Live order tracking
-   📧 Email notifications
-   📱 Push notifications
-   🔐 Secure authentication
-   📊 Seller sales analytics
-   📈 Revenue dashboard
-   🧾 PDF invoice generation
-   🏷️ Discount and coupon system
-   🔎 Advanced product filters
-   📦 Stock alerts
-   👥 Multiple seller accounts
-   🌐 Cloud synchronization

------------------------------------------------------------------------

## 👨‍💻 Project Purpose

ShopSmart demonstrates how Kotlin and Android XML can be used to build a
complete retail-management and shopping application.

The project covers important Android development concepts including:

-   Activities
-   Intents
-   XML layouts
-   Event listeners
-   Local storage
-   JSON data handling
-   CRUD-style product management
-   Cart management
-   Order processing
-   Navigation
-   UI design

------------------------------------------------------------------------

## 📌 Project Status

**ShopSmart -- Android Retail & Shopping Application**

Current implementation includes the seller dashboard, inventory,
customer shopping flow, cart, checkout/order flow, and additional
shopping-management features described above.

> Some advanced features may require further backend integration
> depending on the final project version.

------------------------------------------------------------------------

## 📄 License

This project is intended for educational and academic use.
