package com.example.android.v2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.android.v2.R
import com.example.android.v2.activities.ProductDetailsActivity
import com.example.android.v2.activities.WelcomeActivity
import com.example.android.v2.adapters.BannerAdapter
import com.example.android.v2.adapters.ProductsAdapter
import com.example.android.v2.data.banneritems.BannerItems
import com.example.android.v2.data.banneritems.ZoomOutPageTransformer
import com.example.android.v2.data.products.firebase.ProductRepository
import com.example.android.v2.models.product.ProductFromFirebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class HomeFragment : Fragment() {

    private val welcomeActivity = WelcomeActivity()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val viewPager2 = root.findViewById<ViewPager2>(R.id.viewPager2)
        setUpViewPager(viewPager2)
        val allProducts = welcomeActivity.allProducts
        Log.d("TAG", "onCreateView: $allProducts")
        val productsListRecyclerView = root
            .findViewById<RecyclerView>(R.id.productsListRecyclerView)

//        val categories = resources.getStringArray(R.array.product_categories)

//        CoroutineScope(Dispatchers.Main).launch {
//            categories.forEach {
//                loadProductsList(it.split("-")[0].trim(), allProducts)
//                setUpRecyclerProductView(productsListRecyclerView, allProducts)
//            }
//        }

        return root
    }

    private suspend fun loadProductsList(
        category: String,
        allProducts: ArrayList<ProductFromFirebase>
    ) {
        val productRepository = ProductRepository()
        val productsFromDB = productRepository.getProductsList(category)
            .get().await().documents
        productsFromDB.forEach {
            allProducts.add(
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

    private fun setUpViewPager(view: ViewPager2) {
        val adapter = BannerAdapter(BannerItems.list!!)
        view.adapter = adapter
        view.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        view.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun setUpRecyclerProductView(
        recyclerView: RecyclerView,
        productsList: ArrayList<ProductFromFirebase>
    ) {
        val adapter = context?.let { ProductsAdapter(productsList, it) }
        recyclerView.adapter = adapter
        adapter?.onItemClick = {
            val gson = Gson()
            val productDetails = gson.toJson(it)
            Log.d("TAG", "setUpRecyclerProductView: $productDetails")
            startActivity(
                Intent(context, ProductDetailsActivity::class.java)
                    .putExtra("Product", productDetails)
            )
        }
    }
}