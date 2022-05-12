package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentProductDetailsBinding
import com.mahmoudbashir.realstate.pojo.productModel
import com.squareup.picasso.Picasso

class DetailsProduct_Fragment:Fragment() {

    lateinit var detailsBinding: FragmentProductDetailsBinding
    lateinit var pro_model: productModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        detailsBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_product_details,container,false)
        pro_model = arguments?.getSerializable("proModel") as productModel

        return detailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doBackStack()
        updateViews()

        Toast.makeText(context,"mmmmm mmm ${pro_model.address}",Toast.LENGTH_LONG).show()

    }

    private fun updateViews() {
        Picasso.get().load(pro_model.product_imgs?.get(0)).into(detailsBinding.itemImg)
        detailsBinding.productName.text = pro_model.categoryType
        detailsBinding.productAddress.text = pro_model.address
    }

    private fun doBackStack(){
        detailsBinding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}