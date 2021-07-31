package com.example.android.v2.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.icu.text.DecimalFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.data.cart.CartRepository
import com.example.android.v2.data.cart.DatabaseHelper
import com.example.android.v2.models.product.ProductInCart
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view_product_in_cart_grid_item.view.*
import java.util.*

class CartAdapter(
    private val context: Context,
    private val cart: ArrayList<ProductInCart>,
    private val totalPrice: TextView?
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private lateinit var dialog: AlertDialog
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context, null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_product_in_cart_grid_item, parent, false)
        return CartViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cart[position]
        holder.setData(product)

        holder.itemView.iVAddQuantity.setOnClickListener {
            updateProductQuantityInCart(holder, product, "ADD")
        }

        holder.itemView.iVSubtractQuantity.setOnClickListener {
            if (holder.itemView.txvProductQuantity.text.toString().toInt() == 1) {
                showDialogRemoveProductFromCart(product, position)
            } else {
                updateProductQuantityInCart(holder, product, "SUBTRACT")
            }
        }

        holder.itemView.iVRemoveProduct.setOnClickListener {
            showDialogRemoveProductFromCart(product, position)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProductQuantityInCart(holder: CartViewHolder, product: ProductInCart,
                                            operator: String) {

        val productInCart = CartRepository
                .fetchProductInCart(databaseHelper, productID = product.id)

        holder.itemView.txvProductQuantity.text = if (operator.toUpperCase(Locale.ROOT) == "ADD") {
            (holder.itemView.txvProductQuantity.text.toString().toInt() + 1).toString()
        } else (holder.itemView.txvProductQuantity.text.toString().toInt() - 1).toString()

        val updatedProductTotalPrice = product.price * holder.itemView.txvProductQuantity
                .text.toString()
                .toInt()
        holder.itemView.txvProductTotalPrice.text =
                    DecimalFormat("#,###.##").format(updatedProductTotalPrice)
        val updatedProductQuantity = holder.itemView.txvProductQuantity.text.toString().toInt()
        val updatedProductInCart = ProductInCart(
                id = productInCart!!.id,
                name = productInCart.name,
                price = productInCart.price,
                quantity = updatedProductQuantity,
                category = productInCart.category,
                total = updatedProductTotalPrice,
                imageURL = productInCart.imageURL
        )
        CartRepository.updateProductInCart(databaseHelper, updatedProductInCart)
        updateTotalPrice()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDialogRemoveProductFromCart(product: ProductInCart, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Bạn chắc chắn muốn bỏ sản phẩm ${product.name} khỏi giỏ hàng?")
        val dialogClickListener = DialogInterface.OnClickListener{_, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    removeProductFromCart(product, position)
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }
        builder.setPositiveButton("Đồng ý", dialogClickListener)
        builder.setNegativeButton("Không", dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun removeProductFromCart(product: ProductInCart, position: Int) {
        CartRepository.removeProductInCart(databaseHelper, product.id)
        updateTotalPrice()
        cart.removeAt(position)
        notifyDataSetChanged()
        Toast.makeText(
                context,
                "Đã xoá sản phẩm ${product.name} khỏi giỏ hàng",
                Toast.LENGTH_SHORT
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateTotalPrice() {
        val cart = CartRepository.fetchCart(databaseHelper)
        var total = 0;
        for (product in cart) {
            total += product.total
        }
        totalPrice?.text =  DecimalFormat("#,###.##").format(total)
    }

    override fun getItemCount() = cart.size

    inner class CartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(productInCart: ProductInCart) {
            itemView.txvProductName.text = productInCart.name
            itemView.txvProductPrice.text = DecimalFormat("#,###.##")
                    .format(productInCart.price)
            itemView.txvProductTotalPrice.text = DecimalFormat("#,###.##")
                .format(productInCart.total)
            itemView.txvProductQuantity.text = productInCart.quantity.toString()
            Picasso.get().load(productInCart.imageURL)
                    .resize(300, 300)
                    .centerCrop()
                    .into(itemView.ivProduct)
        }
    }
}