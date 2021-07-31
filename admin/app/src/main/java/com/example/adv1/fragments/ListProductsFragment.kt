package com.example.adv1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.adv1.R
import com.example.adv1.repositories.Repositories
import com.example.adv1.adapters.ProductsAdapter
import com.example.adv1.models.Product
import kotlinx.android.synthetic.main.fragment_list_products.*


class ListProductsFragment : Fragment() {

    private val repositories = Repositories()
    private var productsList = ArrayList<Product>()
    private var productsListRecyclerView: RecyclerView? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_list_products, container,
                false)
        productsListRecyclerView = root.findViewById<RecyclerView>(R.id.productsListRecyclerView)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCategoriesSpinner()
        spinnerListCategories.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View,
                                        position: Int, id: Long) {
                loadProductsList(productsListRecyclerView!!,
                        spinnerListCategories.getItemAtPosition(position).toString()
                                .split("-")[0].trim())
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun setUpCategoriesSpinner() {
        val adapter = context?.let {
            ArrayAdapter.createFromResource(
                    it,
                    R.array.product_categories,
                    R.layout.support_simple_spinner_dropdown_item)
        }
        spinnerListCategories.adapter = adapter
    }

    private fun loadProductsList(productsListRecyclerView: RecyclerView, categorySelected: String) {
        repositories.fetchProducts().document(categorySelected)
                .collection(categorySelected)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        productsList = it.result!!
                                .toObjects(Product::class.java) as ArrayList<Product>
                        setUpRecyclerProductView(productsListRecyclerView)
                    }
                }
    }

    private fun setUpRecyclerProductView(productsListRecyclerView: RecyclerView) {
        val adapter = context?.let { ProductsAdapter(productsList, it) }
        productsListRecyclerView.adapter = adapter
    }
}