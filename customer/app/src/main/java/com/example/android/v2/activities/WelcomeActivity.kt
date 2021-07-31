package com.example.android.v2.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.v2.R
import com.example.android.v2.data.products.firebase.ProductRepository
import com.example.android.v2.models.product.ProductFromFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import render.animations.*
import java.util.ArrayList

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val textP : TextView = findViewById(R.id.textP)
        val textK : TextView = findViewById(R.id.textK)
        val textL : TextView = findViewById(R.id.textL)

        // Create Render Class
        val render = Render(this)
        render.setAnimation(Fade().InDown(textP).setDuration(10000))
        render.setAnimation(Fade().InDown(textK).setDuration(5000))
        render.setAnimation(Fade().InDown(textL).setDuration(3000))
        render.start()

        val categories = resources.getStringArray(R.array.product_categories)
        val allProducts = ArrayList<ProductFromFirebase>()
        CoroutineScope(Dispatchers.Main).launch {
            categories.forEach {
                loadProductsList(it.split("-")[0].trim(), allProducts)
            }
            Handler().postDelayed({
                val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                        .putExtra("AllProducts", allProducts)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

    private suspend fun loadProductsList(
            category: String,
            allProducts: ArrayList<ProductFromFirebase>,
    ) {
        val productRepository = ProductRepository()
        val productsFromDB = productRepository.getProductsList(category)
                .get().await().documents
        productsFromDB.forEach {
            allProducts!!.add(
                    ProductFromFirebase(
                            id = it.get("id").toString(),
                            name = it.get("name").toString(),
                            price = it.get("price").toString().toInt(),
                            category = it.get("category").toString(),
                            imageURL = it.get("imageURL").toString()
                    )
            )
        }
    }
}