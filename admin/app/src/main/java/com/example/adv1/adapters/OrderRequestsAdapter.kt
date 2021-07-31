package com.example.adv1.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adv1.R
import com.example.adv1.activities.OrderDetailsActivity
import com.example.adv1.models.ProductInOrder
import com.example.adv1.models.Request
import com.example.adv1.repositories.Repositories
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.card_view_order_request_grid_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.DecimalFormat


class OrderRequestsAdapter(
    private val requests: ArrayList<Request>,
    private val context: Context
) : RecyclerView.Adapter<OrderRequestsAdapter.OrderRequestsViewHolder>() {

    private lateinit var dialog: AlertDialog
    private val repositories = Repositories()
    private val orderDetails = OrderDetailsActivity()

    @SuppressLint("CommitTransaction")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRequestsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_order_request_grid_item, parent, false)
        return OrderRequestsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderRequestsViewHolder, position: Int) {
        val request = requests[position]
        holder.setData(request)

        holder.itemView.btnViewOrderDetails.setOnClickListener {
            context.startActivity(
                Intent(context, OrderDetailsActivity::class.java)
                    .putExtra("orderID", request.orderID)
                    .putExtra("guestID", request.guestID)
            )
        }

        holder.itemView.btnAppproveOrderRequest.setOnClickListener {
            showDialogManipulateOrder(request, false)
        }

        holder.itemView.btnCancelOrder.setOnClickListener {
            showDialogManipulateOrder(request, true)
        }
    }

    private fun showDialogManipulateOrder(request: Request, rejected: Boolean) {
        val builder = AlertDialog.Builder(context)
        val message = if (rejected) "Huỷ đơn đặt hàng?" else "Xác nhận duyệt đơn đặt hàng?"
        builder.setMessage(message)
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    context
                    if (rejected) {
                        changeOrderState(request, true)
                    } else {
                        approveOrder(request)
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }
        builder.setPositiveButton("Có", dialogClickListener)
        builder.setNegativeButton("Không", dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun approveOrder(request: Request) {
        val productRepository = repositories.fetchProducts()
        CoroutineScope(Main).launch {
            val orderDetails = orderDetails.loadOrderDetails(
                orderID = request.orderID,
                guestID = request.guestID
            )
            var message = ""
            if (checkProductQuantity(orderDetails, productRepository)) {
                updateProductQuantity(orderDetails, productRepository, request)
                message = "Duyệt đơn hàng thành công"
                requests.remove(request)
                notifyDataSetChanged()
            } else {
                message = "Không đủ số lượng hàng hoá trong kho!"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun checkProductQuantity(
        orderDetails: ArrayList<ProductInOrder>,
        productRepository: CollectionReference,
    ): Boolean {
        orderDetails.forEach { productInOrder ->
            val productRef = productRepository
                .document(productInOrder.category)
                .collection(productInOrder.category)
                .whereEqualTo("id", productInOrder.id)
            val product = productRef.get().await().documents
            product.forEach {
                val prevQuantity = it.get("quantity").toString().toInt()
                val currQuantity = prevQuantity - productInOrder.quantity
                if (currQuantity < 0) return false
            }
        }
        return true
    }

    private suspend fun updateProductQuantity(
        orderDetails: ArrayList<ProductInOrder>,
        productRepository: CollectionReference,
        request: Request
    ) {
        orderDetails.forEach { productInOrder ->
            val productRef = productRepository
                .document(productInOrder.category)
                .collection(productInOrder.category)
                .whereEqualTo("id", productInOrder.id)

            val product = productRef.get().await().documents
            product.forEach {
                val prevQuantity = it.get("quantity").toString().toInt()
                val currQuantity = prevQuantity - productInOrder.quantity
                productRepository
                    .document(productInOrder.category)
                    .collection(productInOrder.category)
                    .document(it.id).update("quantity", currQuantity)
                changeOrderState(request, false)
            }
        }
    }

    private fun changeOrderState(request: Request, isRejected: Boolean) {
        val orderRef =  repositories.fetchOrders(request.guestID)
            .document(request.orderID)
        if (isRejected) orderRef.update("rejected", true)
        orderRef.update("done", true)
        requests.remove(request)
        notifyDataSetChanged()
    }

    override fun getItemCount() = requests.size

    inner class OrderRequestsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun setData(request: Request) {
            request.apply {
                itemView.txvUsername.text = this.username
                itemView.txvEmail.text = this.email
                itemView.txvPhone.text = this.phoneNumber
                itemView.txvOrderDate.text = this.orderedDate
                itemView.txvOrderID.text = this.orderID
                itemView.txvAddress.text = this.address
                itemView.txvQuantity.text = this.quantity.toString()
                itemView.txvTotalPrice.text = DecimalFormat("#,###.##")
                    .format(this.total.toString().toInt())
            }
        }
    }
}