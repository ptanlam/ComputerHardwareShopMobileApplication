package com.example.adv1.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.adv1.R
import com.example.adv1.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view_product_grid_item.view.*
import kotlinx.android.synthetic.main.dialog_edit_product.*
import java.text.DecimalFormat

open class ProductsAdapter(private val productsList: ArrayList<Product>, private val context: Context) :
        RecyclerView.Adapter<ProductsAdapter.ProductDetailViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
            .collection("products")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_product_grid_item, parent, false)
        return ProductDetailViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val product = productsList[position]
        holder.setData(product)

        holder.ivDelete.setOnClickListener {
            showDeleteProductDialog(holder, position)
        }

        holder.ivEdit.setOnClickListener {
            showEditProductDialog(holder, position)
        }
    }

    override fun getItemCount() = productsList.size

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDeleteProductDialog(holder: ProductDetailViewHolder, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Xoá sản phẩm!")
        builder.setMessage("Bạn có chắc chắn xoá sản phẩm " +
                "${holder.itemView.tvProductName.text}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        val firestoreDB = FirebaseFirestore.getInstance()
                .collection("products")

        val product = productsList.find { product -> product.id == holder.productID }

        val mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("${holder.productImageURL}")

        builder.setPositiveButton("Có") { dialogInterface, _ ->
            firestoreDB.document("${holder.productCategory}")
                    .collection("${holder.productCategory}")
                    .document("${holder.productID}")
                    .delete()
            productsList.removeAt(position)
            notifyDataSetChanged()
            mStorageRef.delete().addOnCompleteListener {
                Toast.makeText(
                        context,
                        "Đã xoá sản phẩm thành công!",
                        Toast.LENGTH_SHORT
                ).show()
            }
            holder.setData(product!!)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Không") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showEditProductDialog(holder: ProductDetailViewHolder, position: Int) {
        val product = productsList.find { product -> product.id == holder.productID }
        val categories = context.resources
            .getStringArray(R.array.product_categories)
        var catePos = -1
        for (category in categories) {
            if (category.contains(product?.category.toString())) {
                catePos = categories.indexOf(category)
                break
            }
        }
        val updateDialog = (Dialog(context, R.style.Theme_Dialog)).apply {
            setContentView(R.layout.dialog_edit_product)
            inputProductName.setText(productsList[position].name)
            inputProductPrice.setText(productsList[position].price.toString())
            inputProductQuantity.setText(productsList[position].quantity.toString())
            Picasso.get().load(productsList[position].imageURL)
                .resize(300, 300)
                .centerCrop()
                .into(ivItem)
            val adapter = context.let { ArrayAdapter.createFromResource(
                    it,
                    R.array.product_categories,
                    R.layout.support_simple_spinner_dropdown_item)}
            spinnerCategories.adapter = adapter
            spinnerCategories.setSelection(catePos)
        }

        updateDialog.btnUpdateProduct.setOnClickListener {
            if (updateDialog.inputProductName.text.toString().isNotEmpty()
                    && updateDialog.inputProductPrice.text.toString().isNotEmpty()
                    && updateDialog.inputProductQuantity.text.toString().isNotEmpty()) {
                product?.name = updateDialog.inputProductName.text.toString()
                val newCategory =  updateDialog.spinnerCategories.selectedItem.toString()
                        .split("-")[0].trim()
                val priceString = updateDialog.inputProductPrice.text.toString()
                        .split(",")
                var price = ""
                for (each in priceString) price += each
                product?.price = price.toInt()
                product?.quantity = updateDialog.inputProductQuantity.text.toString().toInt()
                if (product?.category == newCategory) {
                    db.document(product.category)
                            .collection(product.category)
                            .document(product.id)
                            .update(
                                    "name", product.name,
                                    "price", product.price,
                                    "quantity", product.quantity
                            ).addOnCompleteListener {
                                Toast.makeText(
                                        context,
                                        "Đã cập nhật sản phẩm thành công",
                                        Toast.LENGTH_LONG
                                ).show()
                                updateDialog.dismiss()
                            }
                } else {
                    val categoryRef = db
                            .document(newCategory)
                            .collection(newCategory).document()
                    if (product != null) {
                        categoryRef
                                .set(product)
                                .addOnCompleteListener {
                                    categoryRef
                                            .update("category", newCategory,
                                                    "id" , categoryRef.id)
                                    Toast.makeText(
                                            context,
                                            "Đã cập nhật sản phẩm thành công",
                                            Toast.LENGTH_LONG
                                    ).show()
                                    updateDialog.dismiss()
                                }
                        db.document("${holder.productCategory}")
                                .collection("${holder.productCategory}")
                                .document("${holder.productID}")
                                .delete()
                        productsList.removeAt(position)
                    }
                }
                notifyDataSetChanged()
            }
            else {
                Toast.makeText(
                        context,
                        "Vui lòng điền đầy đủ thông tin",
                        Toast.LENGTH_LONG
                ).show()
            }
        }

        updateDialog.btnCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    inner class ProductDetailViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

        val ivEdit: ImageView = itemView.ivEdit
        val ivDelete: ImageView = itemView.ivDelete
        private val ivProduct: ImageView = itemView.ivProduct
        var productImageURL: String? = null
        var productID: String? = null
        var productCategory: String? = null

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.N)
        fun setData(product: Product) {
            var colorState: Int = 0
            if (product.quantity == 0) {
                colorState = R.color.colorWarning
                itemView.tvProductName.text = product.name + " (Hết hàng)"
            } else {
                colorState = R.color.colorAvailable
                itemView.tvProductName.text = product.name
            }
            itemView.tvProductPrice.text = DecimalFormat("#,###.##")
                .format(product.price)
            itemView.tvProductQuantity.text = product.quantity.toString()
            itemView.tvProductQuantity.setTextColor(ContextCompat.getColor(context, colorState))
            productID = product.id
            productCategory = product.category
            productImageURL = product.imageURL
            Picasso.get().load(product.imageURL)
                    .resize(300, 300)
                    .centerCrop()
                    .into(ivProduct)
        }
    }
}