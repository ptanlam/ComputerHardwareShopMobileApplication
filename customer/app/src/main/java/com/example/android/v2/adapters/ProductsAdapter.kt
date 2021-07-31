package com.example.android.v2.adapters

import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.data.cart.CartDB
import com.example.android.v2.data.cart.DatabaseHelper
import com.example.android.v2.models.product.ProductFromFirebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view_product_grid_item.view.*
import java.text.DecimalFormat

class ProductsAdapter(
    private val productsList: ArrayList<ProductFromFirebase>,
    private val context: Context
) :
    RecyclerView.Adapter<ProductsAdapter.ProductDetailViewHolder>() {

    private val databaseHelper: DatabaseHelper = DatabaseHelper(context, null)
    var onItemClick: ((ProductFromFirebase) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_product_grid_item, parent, false)
        return ProductDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val product = productsList[position]
        holder.setData(product)
        holder.addToCart.setOnClickListener {
            val msg = saveProductToCart(product)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = productsList.size

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

    inner class ProductDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val addToCart: ImageView = itemView.iVAddToCart

        fun setData(productFromFirebase: ProductFromFirebase) {
            itemView.txvProductName.text = productFromFirebase.name
            itemView.txvProductPrice.text = DecimalFormat("#,###.##")
                .format(productFromFirebase.price)
            Picasso.get().load(productFromFirebase.imageURL)
                .resize(300, 300)
                .centerCrop()
                .into(itemView.ivProduct)
        }

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(productsList[adapterPosition])
            }
        }
    }
}