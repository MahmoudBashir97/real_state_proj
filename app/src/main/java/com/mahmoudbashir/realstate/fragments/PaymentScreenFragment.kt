package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kaopiz.kprogresshud.KProgressHUD
import com.mahmoudbashir.realstate.PaymentScreenFragmentDirections
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentPaymentScreenBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.requestModel
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import java.text.SimpleDateFormat
import java.util.*


class PaymentScreenFragment : Fragment() {
    lateinit var pro_model: productModel
    lateinit var paymentBinding: FragmentPaymentScreenBinding
    lateinit var viewModel:RealStateviewModel
    lateinit var  dialog:KProgressHUD
    var isUploaded=false
    lateinit var requestRef:DatabaseReference

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        paymentBinding =  DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_payment_screen,
                container,
                false
        )
        pro_model = arguments?.getSerializable("proModel") as productModel
        //inirialize ViewModel
        viewModel = (activity as MainActivity).viewModel
        InitializeProgressDialog()


        return paymentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestRef = FirebaseDatabase.getInstance().reference.child("Requests")

        doNewRequest()
        paymentBinding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }


        paymentBinding.edtExDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                if (start == 1 && start+added == 2 && p0?.contains('/') == false) {
                    paymentBinding.edtExDate.setText("/"+p0.toString())
                } else if (start == 3 && start-removed == 2 && p0?.contains('/') == true) {
                    paymentBinding.edtExDate.setText(p0.toString().replace("/", ""))
                }
            }
        })
    }

    private fun doNewRequest()
    {
        paymentBinding.payBtn.setOnClickListener {
            Log.d("pathImg :","services : ${pro_model.services}")
            if (validate()){
                dialog.show()
                addNewRequest(pro_model)
               /* runBlocking {
                    viewModel.uploadNewRequest(pro_model)
                    delay(2500)
                    viewModel.getResponseMessage().observe(viewLifecycleOwner, {
                        if (it == "request_uploaded") {
                            runBlocking { delay(200) }
                            showErrorMessage("Youe request uploaded successfully!")
                            navigateToHome()
                        } else {
                            dialog.dismiss()
                            runBlocking { delay(15000) }
                            showErrorMessage("failed in uploading your new request!")
                        }
                    })
                }*/

            }
        }
    }

    private  fun addNewRequest(model: productModel) {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        val currentDate: String = dateFormat.format(date)
        val requesId = randNumb(32154684,524)
        val userId = SharedPreference.getInastance(context).userId

        Log.d("addNewReq : ",model.productSort.toString())
        val mRequest = requestModel(
            "$requesId",
            userId,
            model.ownerId,
            model.productId,
            model.productSort,
            model.productType,
            model.productPrice,
            model.categoryType,
            model.productDesc,
            model.address,
            model.product_imgs,
            currentDate, model.services!!
        )
        requestRef.child("$requesId").setValue(mRequest)
            .addOnCompleteListener {
                if (it.isSuccessful){
                   dialog.dismiss()
                    showErrorMessage("Youe request uploaded successfully!")
                    navigateToHome()
                }
            }.addOnFailureListener {
                it.message
                dialog.dismiss()
                showErrorMessage("failed in uploading your new request!")
            }
    }

    private fun InitializeProgressDialog(){
         dialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploading your request...")
                .setCancellable(false)
                .setBackgroundColor(requireContext().resources.getColor(R.color.primary_dark))
                .setCornerRadius(8f)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)

    }

    private fun showErrorMessage(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).apply {
            show()
        }
    }


    private fun navigateToHome() {
        dialog.dismiss()
        findNavController().navigate(PaymentScreenFragmentDirections.actionPaymentScreenFragmentToHomeFragment(
                SharedPreference.getInastance(context).userType, "payment")
        )
    }

    private fun validate():Boolean{
        return if (TextUtils.isEmpty(paymentBinding.edtDigits.text.toString())) {
            paymentBinding.edtDigits.error = "Please Enter Your Card Digits."
            paymentBinding.edtDigits.requestFocus()
            false
        } else if (TextUtils.isEmpty(paymentBinding.edtExDate.text.toString())) {
            paymentBinding.edtExDate.error = "Please Enter Your Card Expire Date."
            paymentBinding.edtExDate.requestFocus()
            false
        } else if (TextUtils.isEmpty(paymentBinding.edtPassSecret.text.toString())) {
            paymentBinding.edtPassSecret.error = "Please Enter Your Card Digits."
            paymentBinding.edtPassSecret.requestFocus()
            false
        } else true
    }


    fun randNumb(max: Int, min: Int): Int {
        val rn = Random()
        val n = max - min + 1
        val i: Int = rn.nextInt() % n
        return min + i
    }

}