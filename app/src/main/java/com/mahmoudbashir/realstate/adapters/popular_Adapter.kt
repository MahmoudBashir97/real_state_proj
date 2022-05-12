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
import com.mahmoudbashir.realstate.pojo.productModel
import com.squareup.picasso.Picasso

class popular_Adapter(val context:Context,
                      val mlist:MutableList<productModel>,
                      val itemClickedInterface: ProductItemClickedInterface): RecyclerView.Adapter<popular_Adapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_img = itemView.findViewById<ImageView>(R.id.item_img)
        val txt_category_type = itemView.findViewById<TextView>(R.id.txt_category_type)
        val txt_price = itemView.findViewById<TextView>(R.id.txt_price)
        val txt_address = itemView.findViewById<TextView>(R.id.txt_address)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_item_popular_row,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(mlist[position].product_imgs?.get(0)).into(holder.item_img)
        holder.txt_category_type.text = mlist[position].categoryType
        holder.txt_address.text = mlist[position].address
        holder.txt_price.text = mlist[position].productPrice
        holder.itemView.setOnClickListener {
            itemClickedInterface.onClick(position)
        }
    }

    override fun getItemCount()= mlist.size
}