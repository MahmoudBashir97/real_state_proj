package com.mahmoudbashir.realstate.fragments

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.Utils.Constants
import com.mahmoudbashir.realstate.databinding.FragmentLoiginScreenBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel


class LoginScreenFragment : Fragment() {

    private lateinit var loginBinding:FragmentLoiginScreenBinding
    lateinit var viewModel: RealStateviewModel
    lateinit var auth : FirebaseAuth
    lateinit var reference:DatabaseReference
    var userType:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        loginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_loigin_screen,
            container,
            false
        )


        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(requireActivity())
        reference = FirebaseDatabase.getInstance().reference




        return loginBinding.root
    }

    private fun loginBtnClicked() {
        loginBinding.loginBtn.setOnClickListener {
            if (validateInputs()){
                Constants.hideKeyboard(activity as MainActivity)
                loginBinding.isLoading = true
                doLogin(it)
            }
        }
    }



    private fun setUpViewModel(){
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userType = arguments?.get("userType").toString()

        setUpViewModel() //do setup or initialization for ViewModel
        loginBtnClicked()
        navigateToRegisterScreen(userType!!)

    }

    private fun navigateToRegisterScreen(userType: String) {
        loginBinding.toRegisterBtn.setOnClickListener {
            findNavController().navigate(
                LoginScreenFragmentDirections.actionLoiginScreenFragmentToRegisterScreenFragment(
                    userType
                )
            )
        }
    }

    private fun doLogin(v: View){

        loginBinding.isLoading = true
        val email = loginBinding.edtEmail.text.toString()
        val password = loginBinding.edtPass.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    checkUser(v, email, userType!!)
                    Log.d("RegisterProcessTAG:  ", " = success")
                }
            }.addOnFailureListener { e->
                loginBinding.isLoading = false
                e.message
                Snackbar.make(
                    v,
                    "Please check your Internet connection or Enter a valid email and password!",
                    3000
                ).show()
            }


       /* viewModel.registerLiveData(
                loginBinding.edtEmail.text.toString(),
                loginBinding.edtPass.text.toString(),
                userType!!
        )

        viewModel.checkRegisteration().observe(viewLifecycleOwner,{
            response->
            if (response =="success"){
                navigateToHomeScreen()
                loginBinding.isLoading = false
//                SharedPreference.getInastance(context).save_InfoData(
//                        ,
//                        loginBinding.edtEmail.text.toString())
            }else{
                Snackbar.make(v,"Please check your Internet connection or Enter a valid email and password!",3000).show()
                //Toast.makeText(context,"$response", Toast.LENGTH_LONG).show()
                Log.d("errorLogin : ",response)
                runBlocking {
                    delay(2000)
                    loginBinding.isLoading = false
                }
            }
        })*/
    }

    private fun checkUser(v: View, email: String, pathType: String){
        Log.d("CheckEmailUser: ", email + pathType+" _ "+userType!!)
        reference.child(userType!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (s in snapshot.children) {
                        if (email == s.child("email").value.toString()) {
                            val fullname = s.child("full_name").value.toString()
                            val _id = s.child("id").value.toString()
                            Log.d("CheckEmailUser: ", "checked success $fullname")
                            SharedPreference.getInastance(context).save_InfoData(
                                fullname,
                                email,
                                pathType,
                                _id
                            )
                            loginBinding.isLoading = false
                            navigateToHomeScreen()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("CheckEmailUser: ", "${error.message}")
                loginBinding.isLoading = false
                Snackbar.make(
                    v,
                    "Please check your Internet connection or Enter a valid email and password!",
                    3000
                ).show()
            }
        })
    }

    private fun navigateToHomeScreen() {
        //Toast.makeText(context,"clicked !!",Toast.LENGTH_LONG).show()
        findNavController().navigate(
            LoginScreenFragmentDirections.actionLoiginScreenFragmentToHomeFragment(
                userType!!,
                "login"
            )
        )
    }

    private fun validateInputs():Boolean{
        return if (TextUtils.isEmpty(loginBinding.edtEmail.text)){
            loginBinding.edtEmail.error = "Please Enter a Valid Email address"
            loginBinding.edtEmail.requestFocus()
            false
        } else if (TextUtils.isEmpty(loginBinding.edtPass.text) || loginBinding.edtPass.text.toString().length < 3 ){
            loginBinding.edtPass.error = "Please Enter a valid password with more than 3 digits"
            loginBinding.edtPass.requestFocus()
            false
        }else
            true
    }

}