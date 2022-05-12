package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentSettingsBinding
import com.mahmoudbashir.realstate.local.SharedPreference


class SettingsFragment : Fragment() {

    lateinit var settingsBinding: FragmentSettingsBinding
    var isNavigated = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false)

        return settingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doLogout()
    }

    private fun doLogout() {
        settingsBinding.logoutBtn.setOnClickListener {
            SharedPreference.getInastance(context).clearUser()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWelcomeScreenFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (!isNavigated)
            requireActivity().onBackPressedDispatcher.addCallback(this){
                val navController = findNavController()
                if (navController.currentBackStackEntry?.destination?.id != null) {
                    findNavController().popBackStack(
                            navController.currentBackStackEntry?.destination?.id!!,
                            true
                    )
                } else
                    navController.popBackStack()
            }
    }
}