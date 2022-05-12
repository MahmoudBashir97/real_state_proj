package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentChoosedPathBinding


class choosedPath_Fragment : Fragment() {

    lateinit var choosedBinding:FragmentChoosedPathBinding
    var pathType:String?=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        choosedBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_choosed_path_, container, false)



        return choosedBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pathType = arguments?.get("pathType").toString()

        checkViewState()

    }

    private fun checkViewState(){
        if (pathType.equals("register")){
            choosedBinding.txtRegistTitle.text = "Register"
            navigateToRegister()
        }else{
            choosedBinding.txtRegistTitle.text = "Login"
            navigateToLogin()
        }

    }

    private fun navigateToLogin(){
        choosedBinding.userBtn.setOnClickListener {
            findNavController().navigate(
                choosedPath_FragmentDirections.actionChoosedPathFragmentToLoiginScreenFragment(
                    "Client"
                )
            )
        }

        choosedBinding.ownerBtn.setOnClickListener {
            findNavController().navigate(
                choosedPath_FragmentDirections.actionChoosedPathFragmentToLoiginScreenFragment(
                    "Owner"
                )
            )
        }
    }

    private fun navigateToRegister(){
        choosedBinding.userBtn.setOnClickListener {
            findNavController().navigate(
                choosedPath_FragmentDirections.actionChoosedPathFragmentToRegisterScreenFragment(
                    "Client"
                )
            )
        }

        choosedBinding.ownerBtn.setOnClickListener {
            findNavController().navigate(
                choosedPath_FragmentDirections.actionChoosedPathFragmentToRegisterScreenFragment(
                    "Owner"
                )
            )
        }
    }

}