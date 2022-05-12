package com.mahmoudbashir.realstate.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.adapters.ProductsListInterface
import com.mahmoudbashir.realstate.fragments.AddProperty_Step2Fragment
import com.mahmoudbashir.realstate.repository.Repository
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import com.mahmoudbashir.realstate.viewModel.ViewModelProviderFactory


class MainActivity : AppCompatActivity() {
    lateinit var viewModel : RealStateviewModel
    lateinit var productsListInterface: ProductsListInterface
    lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        this.window.statusBarColor = Color.WHITE

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        setUpViewModel()
        //viewModel.getProductList()
        viewModel.getProductList()
        //viewModel.getRequestsList()


    }

    fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
            show()
        }
    }

    private fun setUpViewModel() {
        val repo = Repository(application)
        val viewModelProviderFactory = ViewModelProviderFactory(application, repo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(RealStateviewModel::class.java)
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

}