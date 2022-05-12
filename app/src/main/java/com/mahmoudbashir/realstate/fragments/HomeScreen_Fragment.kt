package com.mahmoudbashir.realstate.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.adapters.ItemClickedInterface
import com.mahmoudbashir.realstate.adapters.ProductItemClickedInterface
import com.mahmoudbashir.realstate.adapters.items_Adapter
import com.mahmoudbashir.realstate.adapters.popular_Adapter
import com.mahmoudbashir.realstate.databinding.FragmentHomeScreenBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.ItemModel
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class HomeScreen_Fragment : Fragment() ,ProductItemClickedInterface,ItemClickedInterface{

    lateinit var homeScreenBinding: FragmentHomeScreenBinding
    lateinit var pop_adapter:popular_Adapter
    lateinit var adpt_items:items_Adapter
    lateinit var viewModel:RealStateviewModel
    lateinit var productsRef:DatabaseReference
    lateinit var itemRef:DatabaseReference
    var mlist:MutableList<productModel> = ArrayList()
    var itemsList:MutableList<ItemModel> = ArrayList()
    var sortedList:MutableList<productModel> = ArrayList()
    var isNavigated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        homeScreenBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home_screen_, container, false)
        //initialize ViewModel
        viewModel = (activity as MainActivity).viewModel
        productsRef = FirebaseDatabase.getInstance().reference.child("Products")


        return homeScreenBinding.root
    }

    private fun getProList(){

        mlist = viewModel.getProductsBySort()
       /* mlist.clear()
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    mlist.clear()
                    for (sn in snapshot.children) {
                        val model: productModel = sn.getValue(productModel::class.java) as productModel
                        val address = sn.child("address").value.toString()
                        mlist.add(model)
                        Log.d("getProductList: ", "size : ${mlist.size}")
                        Log.d("getProductList: ", "address : ${model.address}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })*/


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemRef = FirebaseDatabase.getInstance().reference

        getProList()
        setupRecyclerView()

        homeScreenBinding.sellRbutton.isChecked = true
        if (homeScreenBinding.sellRbutton.isChecked) setSortedData("sell")
        if (homeScreenBinding.rentRbutton.isChecked) setSortedData("rent")
        homeScreenBinding.radioGroupChoose.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.sell_rbutton){
                sortedList.clear()
                setSortedData("sell")
                homeScreenBinding.txtPopular.text ="Popular"
            }else if (checkedId == R.id.rent_rbutton){
                sortedList.clear()
                setSortedData("rent")
                homeScreenBinding.txtPopular.text ="Popular"
            }else{
                //homeScreenBinding.recPopular.visibility = View.VISIBLE
                homeScreenBinding.txtPopular.text ="Items"
                getItems()
            }
        }
    }

    private fun setSortedData(sort: String) {
       // homeScreenBinding.recPopular.visibility = View.VISIBLE
        sortedList.clear()
        for (elem in mlist){
            if (elem.productSort == sort){
                sortedList.add(elem)
            }
        }
        setupRecyclerView()
        pop_adapter.notifyDataSetChanged()
    }

    private fun getItems(){
        val userType = SharedPreference.getInastance(context).userType
        itemsList.clear()
        itemRef.child("Items")
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            itemsList.clear()
                            if (snapshot.hasChildren()){
                                for (sn in snapshot.children){

                                    val model = sn.getValue(ItemModel::class.java) as ItemModel
                                    itemsList.add(model)

                                }
                            }
                            Log.d("itemsC: ","inside size: "+itemsList.size)
                            setupRecyclerViewItems()
                            adpt_items.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.message
                    }
                })
        Log.d("itemsC: ","outside size: "+itemsList.size)


        if (userType == "Client"){

        }else{

        }
    }


    private fun setupRecyclerView() {
        pop_adapter = popular_Adapter(requireContext(),sortedList,this)
        homeScreenBinding.recPopular.apply {
            setHasFixedSize(true)
            adapter = pop_adapter
        }
    }


    private fun setupRecyclerViewItems() {
        adpt_items = items_Adapter(requireContext(),itemsList,this)
        homeScreenBinding.recPopular.apply {
            setHasFixedSize(true)
            adapter = adpt_items
        }
    }



    override fun onClick(position:Int) {
        val item = ItemModel()
        val model = sortedList[position]
        isNavigated = true
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProdDetailsFragment(model,"home",item))
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

    override fun onResume() {
        super.onResume()
        getProList()
    }

    override fun onItemClicked(position: Int) {
        val prom = productModel()
        val model = itemsList[position]
        isNavigated = true
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProdDetailsFragment(prom,"home",model))
    }
}