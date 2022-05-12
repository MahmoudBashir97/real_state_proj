package com.mahmoudbashir.realstate.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.mahmoudbashir.realstate.R
import com.squareup.picasso.Picasso

class gridImageAdapter(private val mlist:MutableList<Uri>,val onRemoveClicked:RemoveItemInterface):BaseAdapter() {


    interface RemoveItemInterface {
        fun onRemoveItemClicked(position: Int)
    }

    override fun getCount(): Int = mlist.size

    override fun getItem(position: Int): Any = mlist[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v:View  = LayoutInflater.from(parent!!.context).inflate(R.layout.single_gid_item,parent,false)
        val imgV = v.findViewById<ImageView>(R.id.img_item)
        val remove_btn = v.findViewById<ImageView>(R.id.remove_btn)
        Picasso.get().load(mlist[position]).into(imgV)

        remove_btn.setOnClickListener {
            onRemoveClicked.onRemoveItemClicked(position)
        }

        return v
    }
}