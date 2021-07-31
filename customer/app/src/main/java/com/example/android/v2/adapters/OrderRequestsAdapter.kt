package com.example.android.v2.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.activities.personal.RequestDetailsActivity
import com.example.android.v2.models.Request
import kotlinx.android.synthetic.main.card_view_order_request_grid_item.view.*
import java.text.DecimalFormat

class OrderRequestsAdapter(
        private val requests: ArrayList<Request>,
        private val context: Context
) : RecyclerView.Adapter<OrderRequestsAdapter.OrderRequestsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRequestsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_order_request_grid_item, parent, false)
        return OrderRequestsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderRequestsViewHolder, position: Int) {
        val request = requests[position]
        holder.setData(request)

        holder.itemView.btnShowRequestDetails.setOnClickListener {
            context.startActivity(Intent(context, RequestDetailsActivity::class.java)
                    .putExtra("orderID", request.orderID))
        }
    }


    override fun getItemCount() = requests.size

    inner class OrderRequestsViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun setData(request: Request) {
            request.apply {
                itemView.txvOrderDate.text = this.orderedDate
                itemView.txvOrderID.text = this.orderID
                itemView.txvAddress.text = this.address
                itemView.txvQuantity.text = this.quantity.toString()
                itemView.txvTotalPrice.text = DecimalFormat("#,###.##")
                        .format(this.total.toString().toInt())
                if (this.isDone) {
                    if (this.isRejected) {
                        itemView.txvRequestStatus.text = "Bị từ chối"
                        itemView.txvRequestStatus.setTextColor(ContextCompat
                                .getColor(context, R.color.crimson))
                    } else {
                        itemView.txvRequestStatus.text = "Đã được duyệt"
                        itemView.txvRequestStatus.setTextColor(ContextCompat
                                .getColor(context, R.color.green))
                    }
                } else {
                    itemView.txvRequestStatus.text = "Đang chờ duyệt"
                    itemView.txvRequestStatus.setTextColor(ContextCompat
                            .getColor(context, R.color.yellow))
                }
            }
        }
    }
}