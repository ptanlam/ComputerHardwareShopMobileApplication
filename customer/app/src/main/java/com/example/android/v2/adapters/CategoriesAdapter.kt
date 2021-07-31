package com.example.android.v2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.models.Category
import kotlinx.android.synthetic.main.card_view_category_grid_item.view.*

class CategoriesAdapter(private val categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesDetailViewHolder>() {

    var onItemClick: ((Category) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_category_grid_item, parent, false)
        return CategoriesDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoriesDetailViewHolder, position: Int) {
        val category = categories[position]
        holder.setData(category)
    }

    override fun getItemCount(): Int = 7

    inner class CategoriesDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(category: Category) {
            itemView.tVCategory.text = category.name
            itemView.iVCategory.setImageResource(category.image)
        }

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(categories[adapterPosition])
            }
        }
    }
}