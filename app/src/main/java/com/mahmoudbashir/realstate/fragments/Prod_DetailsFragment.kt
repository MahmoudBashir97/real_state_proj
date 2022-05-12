package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.Prod_DetailsFragmentDirections
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentProdDetailsBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.ItemModel
import com.mahmoudbashir.realstate.pojo.Services
import com.mahmoudbashir.realstate.pojo.productModel
import com.squareup.picasso.Picasso


class Prod_DetailsFragment : Fragment() {

    lateinit var detailsBinding:FragmentProdDetailsBinding
    lateinit var pro_model: productModel
    lateinit var item_model: ItemModel
    var path_user_clicked = ""
    var plumber_checked=false
    var carpenter_checked=false
    var electrician_checked=false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        detailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_prod__details, container, false)
        pro_model = arguments?.getSerializable("proModel") as productModel
        item_model = arguments?.getSerializable("itemModel") as ItemModel
        path_user_clicked = arguments?.getString("path_user_clicked").toString()



        return detailsBinding.root
    }

    private fun setUpItemView() {
        Log.d("pathImg : "," , ${item_model.itemImgPath}")
        Picasso.get().load(item_model.itemImgPath).into(detailsBinding.itemImg)
        detailsBinding.txtItemName.text = item_model.itemName
        detailsBinding.txtPrice.text = item_model.itemPrice
        detailsBinding.txtDesc.text = item_model.itemDesc

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userType = SharedPreference.getInastance(context).userType

        if (pro_model.ownerId != null){
            detailsBinding.isItem = false
            detailsBinding.isItemPath = userType == "Client"
            updateViews()
        }else if (item_model.itemId != null){
            detailsBinding.isItem = true
            detailsBinding.isItemPath = false
            setUpItemView()
        }

        doBackStack()
        checkUserType()
        navigateToPaymentScreen()
    }

    private fun checkBoxChecked(){
       /* detailsBinding.checkPlumber.setOnCheckedChangeListener { buttonView, isChecked -> plumber_checked = isChecked }
        detailsBinding.checkCarpenter.setOnCheckedChangeListener { buttonView, isChecked -> carpenter_checked = isChecked }
        detailsBinding.checkElect.setOnCheckedChangeListener { buttonView, isChecked -> electrician_checked = isChecked }*/
        plumber_checked = detailsBinding.checkPlumber.isChecked
        carpenter_checked = detailsBinding.checkCarpenter.isChecked
        electrician_checked = detailsBinding.checkElect.isChecked
    }

    private fun checkUserType() {
        val userType = SharedPreference.getInastance(context).userType
        if (userType == "Client" ){
            detailsBinding.isUser = path_user_clicked != "profile"
        }

        if (pro_model.productSort == "rent"){
            detailsBinding.toPaymentScreen.text = "Rent"
        }else{
            detailsBinding.toPaymentScreen.text = "buy"
        }


    }


    private fun updateViews() {
        Log.d("pathImg : ","proModel ${pro_model.product_imgs?.get(0)}")

        Picasso.get().load(pro_model.product_imgs?.get(0)).into(detailsBinding.itemImg)
        detailsBinding.productName.text = pro_model.categoryType
        detailsBinding.productAddress.text = pro_model.address
        detailsBinding.productDesc.text = pro_model.productDesc
        detailsBinding.productPrice.text = pro_model.productPrice
    }

    private fun navigateToPaymentScreen(){
       detailsBinding.toPaymentScreen.setOnClickListener {
           setProModel()
           findNavController().navigate(Prod_DetailsFragmentDirections.actionProdDetailsFragmentToPaymentScreenFragment(pro_model))
       }
    }

    private fun setProModel() {
        checkBoxChecked()
        val services = Services(plumber_checked,carpenter_checked,electrician_checked)
        Log.d("pathImg: "," prod ${services}")
        if (pro_model.ownerId != null){
            pro_model.services = services
        }else if (item_model.itemId != null){
            detailsBinding.isItemPath = false
            var listm:MutableList<String> = ArrayList()
            listm.add(item_model.itemImgPath.toString())
            pro_model = productModel(
                    item_model.ownerId,
                    item_model.itemId,
                    "sell",
                    "",
                    item_model.itemPrice,
                    item_model.itemName,
                    item_model.itemDesc,
                    "",
                    listm,
                    services
            )
        }
    }


    private fun doBackStack(){
        detailsBinding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}