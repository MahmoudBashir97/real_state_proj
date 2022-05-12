package com.mahmoudbashir.realstate.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.requestModel
import com.squareup.picasso.Picasso

class requestsAdapter(val userType:String,val mlist:MutableList<requestModel>,val requestsClickedInterface: requestsClickedInterface):
    RecyclerView.Adapter<requestsAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val itemImg = itemView.findViewById<ImageView>(R.id.item_img)
        val txt_cat_type = itemView.findViewById<TextView>(R.id.txt_category_type)
        val txt_price = itemView.findViewById<TextView>(R.id.txt_pro_price)
        val txt_date = itemView.findViewById<TextView>(R.id.txt_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val v = LayoutInflater.from(parent.context).inflate(R.layout.single_item_request,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (userType=="Owner"){
            val text = "A new Request for ${mlist[position].productSort}ing \nyour offered ${mlist[position].categoryType}"
            holder.txt_cat_type.text = text
        }else holder.txt_cat_type.text = mlist[position].categoryType


        Picasso.get().load(mlist[position].product_imgs?.get(0)).into(holder.itemImg)
        holder.txt_price.text = mlist[position].productPrice
        holder.txt_date.text = mlist[position].requestDate

        holder.itemView.setOnClickListener {
            requestsClickedInterface.onRequestsClicked(position)
        }
    }

    override fun getItemCount(): Int= mlist.size
}