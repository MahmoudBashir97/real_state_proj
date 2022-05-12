package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentWelcomeScreenBinding
import com.mahmoudbashir.realstate.local.SharedPreference


class WelcomeScreenFragment : Fragment() {

    lateinit var welcomeBinding:FragmentWelcomeScreenBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val statusL = SharedPreference.getInastance(context).isLoggedIn
        if (statusL){
            navigateToHomeScreen()
        }
        welcomeBinding =  DataBindingUtil.inflate(inflater,
            R.layout.fragment_welcome_screen, container, false)

        return welcomeBinding.root
    }

    private fun navigateToHomeScreen() {
        val userType = SharedPreference.getInastance(context).userType
        findNavController().navigate(WelcomeScreenFragmentDirections.actionWelcomeScreenFragmentToHomeFragment(userType,"welcome"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateToChooseState()

        /*navigateToLoginScreen()
        navigateToRegisterScreen()*/
    }

    private fun navigateToChooseState()
    {
     welcomeBinding.loginBtn.setOnClickListener {
         findNavController().navigate(WelcomeScreenFragmentDirections.actionWelcomeScreenFragmentToChoosedPathFragment("login"))
     }

        welcomeBinding.registerBtn.setOnClickListener {
            findNavController().navigate(WelcomeScreenFragmentDirections.actionWelcomeScreenFragmentToChoosedPathFragment("register"))
        }
    }

}