package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.Utils.Constants
import com.mahmoudbashir.realstate.databinding.FragmentRegisterScreenBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashMap


class RegisterScreenFragment : Fragment() {

    lateinit var registerBinding:FragmentRegisterScreenBinding
    lateinit var viewModel:RealStateviewModel
    lateinit var auth : FirebaseAuth
    lateinit var reference:DatabaseReference
    lateinit var registRef:DatabaseReference
    var userType:String?=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        registerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_register_screen, container, false)


        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(requireActivity())

        return registerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userType = arguments?.get("userType").toString()


        //do setup or initialization for ViewModel
        setUpViewModel()
        registerBtnCLicked()

        reference = FirebaseDatabase.getInstance().reference.child("Users")
        registRef = FirebaseDatabase.getInstance().reference
    }

    private fun setUpViewModel(){
        viewModel = (activity as MainActivity).viewModel
    }

    private fun registerBtnCLicked(){
        registerBinding.edtRegisterBtn.setOnClickListener {

            if (validateInputs()){
                Constants.hideKeyboard(activity as MainActivity)
                registerBinding.isLoading = true
                doRegisting(it)
                //Toast.makeText(context,"Validation is Successfully! ",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun doRegisting(v:View){


        runBlocking { delay(900) }

        val fullName = registerBinding.edtFullname.text.toString()
        val email = registerBinding.edtEmail.text.toString()
        val password =registerBinding.edtPass.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    sentInfoToRealTimeDB(v,fullName, email, userType!!)
                }
            }.addOnFailureListener { e->
                registerBinding.isLoading = false
                e.message
                Snackbar.make(v,"Please check your Internet connection or Enter a valid email and password!",3000).show()
            }

       /* viewModel.registerLiveData(
                registerBinding.edtFullname.text.toString(),
                registerBinding.edtEmail.text.toString(),
                registerBinding.edtPass.text.toString(),
            userType!!
        )

        viewModel.checkRegisteration().observe(viewLifecycleOwner,{
            response->
            if (response =="success"){
                navigateToHomeScreen()
                registerBinding.isLoading = false
               *//* SharedPreference.getInastance(context).save_InfoData(
                        registerBinding.edtFullname.text.toString(),
                        registerBinding.edtEmail.text.toString(),
                        userType!!
                )*//*
            }else{
                Snackbar.make(v,"Please check your Internet connection or Enter a valid email and password!",3000).show()
                Toast.makeText(context, response,Toast.LENGTH_LONG).show()
                runBlocking {
                    delay(2000)
                    registerBinding.isLoading = false
                }
            }
        })*/
    }
    private fun sentInfoToRealTimeDB(v: View,fname: String, email: String, pathType: String){

        val _id= randNumb(1220254, 263)
        var map = HashMap<String, Any>()

        map["full_name"] = fname
        map["email"] = email
        map["id"] = "$_id"

        registRef.child(pathType).child("$_id").setValue(map)
            .addOnCompleteListener {
                registerBinding.isLoading = false
                navigateToHomeScreen()
                SharedPreference.getInastance(context)
                    .save_InfoData(fname,
                        email,
                        pathType,
                        "$_id")
                Log.d("RegisterProcessTAG:  ", " = success")

            }.addOnFailureListener { e->
                registerBinding.isLoading = false
                e.message
                Snackbar.make(v,"Please check your Internet connection or Enter a valid email and password!",3000).show()
            }

    }

    fun randNumb(max: Int, min: Int): Int {
        val rn = Random()
        val n = max - min + 1
        val i: Int = rn.nextInt() % n
        return min + i
    }

    private fun navigateToHomeScreen() {
        findNavController().navigate(RegisterScreenFragmentDirections.actionRegisterScreenFragmentToHomeFragment(userType!!,"register"))
    }


    private fun validateInputs():Boolean{
        return if (TextUtils.isEmpty(registerBinding.edtFullname.text)){
            registerBinding.edtFullname.error = "Please Enter Your Fullname"
            registerBinding.edtFullname.requestFocus()
            false
        }else if (TextUtils.isEmpty(registerBinding.edtEmail.text)){
            registerBinding.edtEmail.error = "Please Enter a Valid Email address"
            registerBinding.edtEmail.requestFocus()
            false
        } else if (TextUtils.isEmpty(registerBinding.edtPass.text) || registerBinding.edtPass.text.toString().length < 3 ){
            registerBinding.edtPass.error = "Please Enter a valid password with more than 3 digits"
            registerBinding.edtPass.requestFocus()
            false
        }else
            true
    }

}