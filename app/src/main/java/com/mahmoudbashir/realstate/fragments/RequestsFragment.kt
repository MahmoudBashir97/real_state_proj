package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.adapters.requestsAdapter
import com.mahmoudbashir.realstate.adapters.requestsClickedInterface
import com.mahmoudbashir.realstate.databinding.FragmentRequestsBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.requestModel
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class RequestsFragment : Fragment() ,requestsClickedInterface{

    lateinit var requestsBinding: FragmentRequestsBinding
    lateinit var viewModel:RealStateviewModel
    lateinit var reqAdapter:requestsAdapter
    var mlist:MutableList<requestModel> = ArrayList()
    var sortedList:MutableList<requestModel> = ArrayList()
    var myId:String=""
    var isNavigated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requestsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_requests, container, false)
        viewModel = (activity as MainActivity).viewModel

        myId = SharedPreference.getInastance(context).userId

        return requestsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getProList()
    }

    private fun setUpRecyclerView() {
        val userType = SharedPreference.getInastance(context).userType
        reqAdapter = requestsAdapter(userType,sortedList,this)
        requestsBinding.recRequests.apply {
            setHasFixedSize(true)
            adapter = reqAdapter
        }
    }

    private fun getProList(){
        sortedList.clear()

        val userType = SharedPreference.getInastance(context).userType
       // Toast.makeText(context,myId,Toast.LENGTH_LONG).show()
        requestsBinding.isLoaded = false
        runBlocking {
          //  delay(200)
            if (userType == "Client"){
                checkForUser()
            }else{
                checkForOwner()
            }

            Log.d("getProductList:","requestsSize : ${mlist.size}")
        }
    }

    private fun checkForUser(){
        sortedList.clear()
        mlist.clear()
        mlist = viewModel.getRequestsList()
        Log.d("mlistSize: ","${mlist.size}")
        for (elem in mlist){
            if (elem.userId == myId){
                sortedList.add(elem)
            }
        }
        if (sortedList.size > 0) requestsBinding.isLoaded = true
        setUpRecyclerView()
        reqAdapter.notifyDataSetChanged()
    }

    private fun checkForOwner(){
        sortedList.clear()
        mlist.clear()
        mlist = viewModel.getRequestsList()
        Log.d("mlistSize: ","${mlist.size}")
        //Toast.makeText(context,"size :${mlist.size}",Toast.LENGTH_LONG).show()
        for (elem in mlist){
            if (elem.ownerId == myId){
                sortedList.add(elem)
            }
        }

        if (sortedList.size > 0) requestsBinding.isLoaded = true
        setUpRecyclerView()
        reqAdapter.notifyDataSetChanged()
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

    override fun onRequestsClicked(position: Int) {

    }
}