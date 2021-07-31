package com.example.android.v2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.v2.R
import com.example.android.v2.data.banneritems.BannerItem
import kotlinx.android.synthetic.main.card_view_banner_item_page.view.*


class BannerAdapter(private val bannerItemList: List<BannerItem>) : RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_banner_item_page, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = bannerItemList.size

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        myViewHolder.setData(bannerItemList[position])
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(bannerItem: BannerItem) {
            itemView.rootCardView.setBackgroundResource(bannerItem.image)
        }
    }
}