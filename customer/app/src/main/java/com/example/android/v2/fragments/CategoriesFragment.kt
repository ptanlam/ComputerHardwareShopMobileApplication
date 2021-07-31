package com.example.android.v2.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.activities.ListProductActivity
import com.example.android.v2.R
import com.example.android.v2.adapters.CategoriesAdapter
import com.example.android.v2.models.Category
import java.util.*


class CategoriesFragment : Fragment() {

    private var categories = arrayListOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_categories, container, false)
        val categoriesRecyclerView = root
            .findViewById<RecyclerView>(R.id.categoriesRecyclerView)
        setUpCategories()
        setUpRecyclerCategoryView(categoriesRecyclerView)
        return root
    }

    private fun setUpCategories() {
        val categories = resources.getStringArray(R.array.product_categories)
        for (each in categories) {
            val image = resources
                .getIdentifier("category_${each.toLowerCase(Locale.ROOT)
                        .split("-")[0].trim()}",
                    "drawable", activity?.packageName)
            val category = Category(each, image)
            this.categories.add(category)
        }
    }

    private fun setUpRecyclerCategoryView(recyclerView: RecyclerView) {
        val adapter = context?.let { CategoriesAdapter(categories) }
        recyclerView.adapter = adapter
        adapter?.onItemClick = {
            startActivity(Intent(context, ListProductActivity::class.java)
                .putExtra("Category", it.name))
        }
    }
}