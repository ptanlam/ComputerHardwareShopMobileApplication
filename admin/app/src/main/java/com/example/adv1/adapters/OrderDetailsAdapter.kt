package com.example.adv1.adapters

import android.icu.text.DecimalFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.adv1.R
import com.example.adv1.models.ProductInOrder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view_product_in_cart_grid_item.view.*
import java.util.ArrayList

class OrderDetailsAdapter(
    private val cart: ArrayList<ProductInOrder>,
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_product_in_cart_grid_item, parent, false)
        return OrderDetailsViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val product = cart[position]
        holder.setData(product)
    }

    override fun getItemCount() = cart.size

    inner class OrderDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(product: ProductInOrder) {
            itemView.txvProductName.text = product.name
            itemView.txvProductPrice.text = DecimalFormat("#,###.##")
                .format(product.price)
            itemView.txvProductTotalPrice.text = DecimalFormat("#,###.##")
                .format(product.total)
            itemView.txvProductQuantity.text = product.quantity.toString()
            Picasso.get().load(product.imageURL)
                .resize(300, 300)
                .centerCrop()
                .into(itemView.ivProduct)
        }
    }
}