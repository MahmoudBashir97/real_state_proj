package com.mahmoudbashir.realstate.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.Utils.Constants
import com.mahmoudbashir.realstate.pojo.ItemModel
import com.mahmoudbashir.realstate.pojo.productModel
import com.squareup.picasso.Picasso

class items_Adapter(val context:Context,
                    val mlist:MutableList<ItemModel>,
                    val itemClickedInterface: ItemClickedInterface): RecyclerView.Adapter<items_Adapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_img = itemView.findViewById<ImageView>(R.id.item_img)
        val txt_item_name = itemView.findViewById<TextView>(R.id.txt_item_name)
        val txt_price = itemView.findViewById<TextView>(R.id.txt_price)
        val txt_desc = itemView.findViewById<TextView>(R.id.txt_desc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_items_row,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(mlist[position].itemImgPath).into(holder.item_img)
        holder.txt_item_name.text = mlist[position].itemName
        holder.txt_desc.text = mlist[position].itemDesc
        holder.txt_price.text = mlist[position].itemPrice
        holder.itemView.setOnClickListener {
            itemClickedInterface.onItemClicked(position)
        }
    }

    override fun getItemCount()= mlist.size
}