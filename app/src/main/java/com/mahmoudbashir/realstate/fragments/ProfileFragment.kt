package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.adapters.ItemClickedInterface
import com.mahmoudbashir.realstate.adapters.ProductItemClickedInterface
import com.mahmoudbashir.realstate.adapters.popular_Adapter
import com.mahmoudbashir.realstate.databinding.FragmentProfileBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class ProfileFragment : Fragment() ,ProductItemClickedInterface{

    lateinit var profileBinding:FragmentProfileBinding
    lateinit var pop_adapter: popular_Adapter
    lateinit var viewModel: RealStateviewModel
    var mlist:MutableList<productModel> = ArrayList()
    var sortedList:MutableList<productModel> = ArrayList()
    var fname:String?=""
    var email:String?=""
    var isNavigated = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false)
        //initialize ViewModel
        viewModel = (activity as MainActivity).viewModel
        getProList()

        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // getStoredInfo()
        setSortedData()
    }

    private fun getProList(){
        runBlocking {
            delay(200)
            mlist = viewModel.getProductsBySort()
            Log.d("getProductList:","SMize : ${mlist.size}")
        }
    }


    private fun setSortedData() {
        val userId = SharedPreference.getInastance(context).userId
        sortedList.clear()
        for (elem in mlist){
            if (elem.ownerId == userId){
                sortedList.add(elem)
                //Toast.makeText(context,"sort: $sort : elementSort: ${sortedList.size}",Toast.LENGTH_LONG).show()
            }
        }
        setupRecyclerView()
        pop_adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        pop_adapter = popular_Adapter(requireContext(),sortedList,this)
        profileBinding.recPopular.apply {
            setHasFixedSize(true)
            adapter = pop_adapter
        }

    }

    private fun getStoredInfo() {
        fname = SharedPreference.getInastance(context).fullName
        email = SharedPreference.getInastance(context).email

        profileBinding.txtFullName.text = fname
        profileBinding.txtEmail.text = email
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

    override fun onClick(position: Int) {

    }

    override fun onDetach() {
        super.onDetach()
        setSortedData()
    }
}