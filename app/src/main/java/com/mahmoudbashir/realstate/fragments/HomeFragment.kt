package com.mahmoudbashir.realstate.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.databinding.FragmentHomeBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel

class HomeFragment : Fragment() ,BottomNavigationView.OnNavigationItemSelectedListener{

    private lateinit var HomeBinding:FragmentHomeBinding
    var userType:String? =""
    var userPath:String? =""
    lateinit var viewModel: RealStateviewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       HomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)
        //initialize ViewModel
        viewModel = (activity as MainActivity).viewModel
        HomeBinding.botomNav.setOnNavigationItemSelectedListener(this)

        return HomeBinding.root
    }

    private fun replacementFrag(frag:Fragment)
    {
        try {
            val fragmanager = childFragmentManager
            fragmanager.beginTransaction().replace(R.id.frame_layout,frag).commit()
        }catch (ex:Exception){
            ex.message
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userType = arguments?.get("userType").toString()
        userPath = arguments?.get("userPath").toString()
        viewModel.getRequests()
       // if (userPath == "property") showErrorMessage("Data has been uploaded successfully.")
        checkUserType(userType!!)
        operateBottomNav()

        HomeBinding.addPropertyFabBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPropertyStep1Fragment())
        }

        HomeBinding.addItemFabBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddItemsFragment())
        }

    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).apply {
            show()
        }
    }

    private fun operateBottomNav() {
        // HomeBinding.botomNav.setupWithNavController(HomeBinding.navHostFragmentContainer.findNavController())
        if (userType == "Client") {
            HomeBinding.botomNav.menu.findItem(R.id.favouriteFragment).isVisible =false
        }
        HomeBinding.botomNav.selectedItemId = R.id.homeScreen_Fragment
        HomeBinding.botomNav.menu.findItem(R.id.homeScreen_Fragment).isChecked = true
    }

    private fun checkUserType(userType: String) {
        if (userType == "Client"){
            HomeBinding.mainFab.visibility = View.GONE
            HomeBinding.addPropertyFabBtn.visibility = View.GONE
            HomeBinding.addItemFabBtn.visibility = View.GONE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_btn -> doLogOut()
            R.id.profileFragment -> {
                if (userType == "Client") {

                    replacementFrag(UserProfileFragment())
                }else{
                    //viewModel.getProductsBySort()
                    replacementFrag(ProfileFragment())
                }
            }
            R.id.favouriteFragment -> replacementFrag(RequestsFragment())
            R.id.homeScreen_Fragment -> replacementFrag(HomeScreen_Fragment())
            else -> replacementFrag(HomeScreen_Fragment())
        }
        return true
    }

    private fun doLogOut(){
        val dialog = AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage("are your sure to exit?")
            .setNegativeButton("cancel") { dialog, which ->
                dialog.dismiss()
            }.setPositiveButton("ok") { dialog, which ->
                SharedPreference.getInastance(context).clearUser()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWelcomeScreenFragment())
            }
        dialog.show()
    }
}