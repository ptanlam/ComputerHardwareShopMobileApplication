package com.example.android.v2.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.adapters.ProductsAdapter
import com.example.android.v2.data.products.firebase.ProductRepository
import com.example.android.v2.models.product.ProductFromFirebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


class ListProductActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product)
        val categoryName = intent.getStringExtra("Category")
        supportActionBar?.title = categoryName?.toUpperCase(Locale.ROOT)
        val productsListRecyclerView =
            findViewById<RecyclerView>(R.id.productsListRecyclerView)
        CoroutineScope(Dispatchers.Main).launch {
            setUpRecyclerProductView(
                productsListRecyclerView,
                loadProductsList(categoryName!!.split("-")[0].trim())
            )
        }
        fABCart.setOnClickListener {
            startActivity(Intent(this, CartInvoiceActivity::class.java))
        }

        productsListRecyclerView.setOnScrollChangeListener { _, _, scrollY, _,
                                                             oldScrollY ->
            val x = scrollY - oldScrollY
            if (x > 0) {
                fABCart.hide()
            } else if (x < 0) {
                fABCart.show()
            }
        }
    }

    private suspend fun loadProductsList(category: String):
            ArrayList<ProductFromFirebase> {
        val productsList = ArrayList<ProductFromFirebase>()
        val productRepository = ProductRepository()
        val productsFromDB = productRepository.getProductsList(category)
            .get().await().documents
        productsFromDB.forEach {
            productsList.add(
                ProductFromFirebase(
                    id = it.get("id").toString(),
                    name = it.get("name").toString(),
                    price = it.get("price").toString().toInt(),
                    category = it.get("category").toString(),
                    imageURL = it.get("imageURL").toString()
                )
            )
        }
        return productsList
    }

    private fun setUpRecyclerProductView(
        recyclerView: RecyclerView,
        productsList: ArrayList<ProductFromFirebase>
    ) {
        val adapter = ProductsAdapter(productsList, this)
        recyclerView.adapter = adapter
        adapter.onItemClick = {
            val gson = Gson()
            val productDetails = gson.toJson(it)
            Log.d("TAG", "setUpRecyclerProductView: $productDetails")
            startActivity(
                Intent(this, ProductDetailsActivity::class.java)
                    .putExtra("Product", productDetails)
            )
        }
    }
}