package com.example.android.v2.adapters

import android.icu.text.DecimalFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.models.product.ProductInCart
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view_product_in_cart_grid_item.view.*

class RequestDetailsAdapter (
    private val cart: ArrayList<ProductInCart>
    ) : RecyclerView.Adapter<RequestDetailsAdapter.RequestDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_product_in_order_cart_grid_item, parent, false)
        return RequestDetailsViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RequestDetailsViewHolder, position: Int) {
        val product = cart[position]
        holder.setData(product)
    }

    override fun getItemCount() = cart.size

    inner class RequestDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(product: ProductInCart) {
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