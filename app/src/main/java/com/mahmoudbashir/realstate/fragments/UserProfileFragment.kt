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
import com.google.firebase.database.*
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.adapters.requestsAdapter
import com.mahmoudbashir.realstate.adapters.requestsClickedInterface
import com.mahmoudbashir.realstate.databinding.FragmentUserProfileBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.ItemModel
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.requestModel
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import kotlinx.coroutines.runBlocking


class UserProfileFragment : Fragment() ,requestsClickedInterface{

    lateinit var userProfBinding:FragmentUserProfileBinding
    lateinit var viewModel: RealStateviewModel
    lateinit var reqAdapter: requestsAdapter
    var mlist:MutableList<requestModel> = ArrayList()
    var sortedList:MutableList<requestModel> = ArrayList()
    lateinit var requestRef:DatabaseReference
    var myId:String=""
    var isNavigated = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userProfBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false)
        viewModel = (activity as MainActivity).viewModel

        myId = SharedPreference.getInastance(context).userId
        requestRef = FirebaseDatabase.getInstance().reference.child("Requests")
        userProfBinding.isLoaded = false

        return userProfBinding.root
    }

    private fun setUpRecyclerView() {
        val userType = SharedPreference.getInastance(context).userType
        reqAdapter = requestsAdapter(userType!!,mlist,this)
        userProfBinding.recRequests.apply {
            setHasFixedSize(true)
            adapter = reqAdapter
        }
    }


    private fun getRequests() {
        userProfBinding.isLoaded = false
        sortedList.clear()
        mlist.clear()
        requestRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()){
                    userProfBinding.isLoaded = false
                    for (sn in snapshot.children){
                        val userId = sn.child("userId").value.toString()
                        if (userId == myId){
                            val model =sn.getValue(requestModel::class.java)
                            if (model != null) {
                                mlist.add(model)
                                Log.d("requestsSizeList:","requestsSize : ${mlist.size}")
                            }
                        }
                    }
                   // userProfBinding.isLoaded = true
                    if (mlist.size > 0) userProfBinding.isLoaded = true
                    setUpRecyclerView()
                    reqAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.message
                userProfBinding.isLoaded = false
            }
        })

//         for (elem in mlist){
//                               if (elem.userId == myId){
//                                   sortedList.add(elem)
//                               }
//                           }

    }


    private fun getProList(){
        sortedList.clear()
        userProfBinding.isLoaded = false
        runBlocking {
            mlist = viewModel.getRequestsList()
            for (elem in mlist){
                if (elem.userId == myId){
                    sortedList.add(elem)
                }
            }
            if (sortedList.size > 0) userProfBinding.isLoaded = true
            setUpRecyclerView()
            reqAdapter.notifyDataSetChanged()
            Log.d("getProductList:","requestsSize : ${mlist.size}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // getProList()
        getRequests()
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
        val item = mlist[position] as requestModel
        val model = productModel(item.ownerId,
        item.productId,
        item.productSort,
        item.productType,
        item.productPrice,
        item.categoryType,
        item.productDesc,
        item.address,
        item.product_imgs,
        item.services)
        val itemM=ItemModel()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProdDetailsFragment(model,"profile",itemM))
    }
}