package com.mahmoudbashir.realstate.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.Utils.Constants
import com.mahmoudbashir.realstate.databinding.FragmentAddPropertyStep1Binding
import com.mahmoudbashir.realstate.pojo.step1Model
import com.mahmoudbashir.realstate.ui.MainActivity


class AddProperty_Step1Fragment : Fragment() {

    lateinit var step1Binding: FragmentAddPropertyStep1Binding
     var selectedCategory:String?=""
     var selectedPropertyType:String?=""
     var selectedPropertySort:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        step1Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_property__step1, container, false)

//        selectedCategory = null.toString()
//        selectedPropertyType = null.toString()
//        selectedPropertySort = null.toString()

        return step1Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateUpToPrevious()
        selectPropertySort()
        selectPropertyType()
        selectCategory()
        submitClicked()

        }

    private fun submitClicked() {
        step1Binding.goStepBtn.setOnClickListener {
        if (validateData()){
            Constants.hideKeyboard(activity as MainActivity)
            val model  = step1Model(
                selectedPropertySort!!,
                selectedPropertyType!!,
                selectedCategory!!,
                step1Binding.productDesc.text.toString(),
                step1Binding.productPrice.text.toString()
            )
            navigateToNextStep(model)
          }
        }
    }

    private fun validateData():Boolean{

        return if (selectedPropertySort == null || selectedPropertySort == ""){
            showErrorMessage("Please select SELL or Rent")
            false
        }else if (selectedPropertyType == null || selectedPropertyType== ""){
            showErrorMessage("Please select Commercial or Residential")
            false
        }else if (selectedCategory == null || selectedCategory== ""){
            showErrorMessage("Please select category type: House or Building or Apartment")
            false
        }else if (TextUtils.isEmpty(step1Binding.productDesc.text.toString())){
            step1Binding.productDesc.error = "Please Enter at least one line description."
            step1Binding.productDesc.requestFocus()
            false
        }else if (TextUtils.isEmpty(step1Binding.productPrice.text.toString())){
            step1Binding.productDesc.error = "Please Enter a price for your product."
            step1Binding.productDesc.requestFocus()
            false
        }else true
    }

    private fun showErrorMessage(message:String){
        /*Snackbar.make(v,message,3000).apply {
            setBackgroundTint(Color.RED)
        }*/
        Toast.makeText(context,message,Toast.LENGTH_LONG).apply {
            show()
        }
    }

    private fun selectPropertySort() {
        step1Binding.rgSortProperty.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_sell){
                selectedPropertySort="sell"
            }
            if (checkedId == R.id.rb_rent){
                selectedPropertySort = "rent"
            }
        }
    }

    private fun selectPropertyType() {
        step1Binding.rgPropertyType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_commercial){
                selectedPropertyType="commercial"
            }
            if (checkedId == R.id.rb_residental){
                selectedPropertyType = "residential"
            }
        }
    }


    private fun selectCategory() {
        step1Binding.rbBuilding.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                selectedCategory = "building"
                step1Binding.rbHouse.isChecked = false
                step1Binding.rbApartment.isChecked = false
            }
        }

        step1Binding.rbApartment.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                selectedCategory = "apartment"
                step1Binding.rbHouse.isChecked = false
                step1Binding.rbBuilding.isChecked = false
            }
        }

        step1Binding.rbHouse.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                selectedCategory = "house"
                step1Binding.rbBuilding.isChecked = false
                step1Binding.rbApartment.isChecked = false
            }
    }
    }

    private fun navigateToNextStep( model: step1Model) {
            findNavController().navigate(AddProperty_Step1FragmentDirections.actionAddPropertyStep1FragmentToAddPropertyStep2Fragment(model))
    }

    private fun navigateUpToPrevious() {
        step1Binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

    }
}