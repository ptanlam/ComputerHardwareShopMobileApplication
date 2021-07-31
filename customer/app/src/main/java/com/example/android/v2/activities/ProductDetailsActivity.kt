package com.example.android.v2.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.v2.R
import com.example.android.v2.adapters.ProductsAdapter
import com.example.android.v2.data.cart.CartDB
import com.example.android.v2.data.cart.DatabaseHelper
import com.example.android.v2.data.products.firebase.ProductRepository
import com.example.android.v2.models.product.ProductFromFirebase
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.card_view_product_grid_item.view.*
import java.text.DecimalFormat

class ProductDetailsActivity : AppCompatActivity() {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Thông tin sản phẩm"
        setContentView(R.layout.activity_product_details)
        val gson = Gson()
        val productDetailsJson = intent.getStringExtra("Product")
        val product = gson
            .fromJson(productDetailsJson, ProductFromFirebase::class.java)

        Picasso.get().load(product.imageURL)
            .into(ivProductDetail)
        tvProductDetailName.text = product.name
        tvProductDetailID.text = product.id
        tvProductDetailPrice.text = DecimalFormat("#,###.##")
            .format(product.price)

        btnAddToCart.setOnClickListener {
            Toast.makeText(this, saveProductToCart(product), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProductToCart(product: ProductFromFirebase): String {
        val db = databaseHelper.readableDatabase
        val message: String?
        val cursor = db.rawQuery(
            "SELECT * FROM ${CartDB.ProductEntry.TABLE_NAME} " +
                    "WHERE _id = '${product.id}'", null
        )
        message = if (cursor.count == 0) {
            val values = ContentValues()
            values.put(CartDB.ProductEntry.COLUMN_ID, product.id)
            values.put(CartDB.ProductEntry.COLUMN_NAME, product.name)
            values.put(CartDB.ProductEntry.COLUMN_PRICE, product.price)
            values.put(CartDB.ProductEntry.COLUMN_QUANTITY, 1)
            values.put(CartDB.ProductEntry.COLUMN_TOTAL, product.price)
            values.put(CartDB.ProductEntry.COLUMN_CATEGORY, product.category)
            values.put(CartDB.ProductEntry.COLUMN_IMAGE_URL, product.imageURL)
            db.insert(CartDB.ProductEntry.TABLE_NAME, null, values)

            "Đã thêm vào giỏ hàng thành công!"
        } else "Bạn đã thêm sản phẩm này vào giỏ hàng rồi!"
        cursor.close()
        return message
    }
}