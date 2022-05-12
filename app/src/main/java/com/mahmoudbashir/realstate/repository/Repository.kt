package com.mahmoudbashir.realstate.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.requestModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Repository(val application: Application) : IRepository {

    private val mauth = FirebaseAuth.getInstance()
    var responseMessage = MutableLiveData<String>()
    var mlist:MutableList<productModel> = ArrayList()
    var requestlist:MutableList<requestModel> = ArrayList()
    var productListResponse = MutableLiveData<MutableList<productModel>>()
    var requestListResponse = MutableLiveData<MutableList<requestModel>>()
    var reference = FirebaseDatabase.getInstance().reference
    var requestRef = FirebaseDatabase.getInstance().reference.child("Requests")
    var productsRef = FirebaseDatabase.getInstance().reference.child("Products")


    override suspend fun RegisterNewAccountToFirebase(fullName: String, email: String, password: String, pathType: String) {
        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                     sentInfoToRealTimeDB(fullName, email, pathType)
                    }
                }.addOnFailureListener { e->
                    responseMessage.postValue("Error ${e.message}")
                }
    }

    override suspend fun LoginWithFirebase(email: String, password: String, pathType: String) {

        mauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        checkUser(email, pathType)
                        Log.d("RegisterProcessTAG:  ", " = success")
                    }
                }.addOnFailureListener { e->
                        responseMessage.postValue("Error ${e.message}")
                }
    }

    override suspend fun uploadNewProduct(productModel: productModel) {
        productsRef.child(productModel.productId!!)
                .setValue(productModel)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        responseMessage.postValue("uploaded")
                        Log.d("listImg : ", "${productModel.product_imgs!!.size} uploadedddd")
                    }
                }.addOnFailureListener {
                    it.message
                    responseMessage.postValue("upload failed")
                }
    }

    override suspend fun getProductList() {

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    mlist.clear()
                    for (sn in snapshot.children) {
                        val model: productModel = sn.getValue(productModel::class.java) as productModel
                        val address = sn.child("address").value.toString()
                        mlist.add(model)
                        productListResponse.postValue(mlist)
                        Log.d("getProductList: ", "size : ${mlist.size}")
                        Log.d("getProductList: ", "address : ${model.address}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })
    }

    override suspend fun addNewRequest(model: productModel) {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        val currentDate: String = dateFormat.format(date)
        val requesId = randNumb(32154684,524)
        val userId = SharedPreference.getInastance(application).userId

        Log.d("addNewReq : ",model.productSort.toString())
        val mRequest = requestModel(
                "$requesId",
                userId,
                model.ownerId,
                model.productId,
                model.productSort,
                model.productType,
                model.productPrice,
                model.categoryType,
                model.productDesc,
                model.address,
                model.product_imgs,
                currentDate,model.services!!
        )
        requestRef.child("$requesId").setValue(mRequest)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        responseMessage.postValue("request_uploaded")
                    }
                }.addOnFailureListener {
                    it.message
                    responseMessage.postValue("error")
                }
    }

    override suspend fun getRequests() {

        requestRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()){
                    requestlist.clear()
                    for (sn in snapshot.children){
                        val model =sn.getValue(requestModel::class.java)
                        if (model != null) {
                            Log.d("mlistSize: ","$model")
                            requestlist.add(model)
                        }
                        requestListResponse.postValue(requestlist)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.message
            }
        })
    }


    private fun checkUser(email: String, pathType: String){
        //Log.d("CheckEmailUser: ","checked success")
        reference.child(pathType).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (s in snapshot.children) {
                        if (email == s.child("email").value.toString()){
                        val fullname = s.child("full_name").value.toString()
                        val _id = s.child("id").value.toString()
                        Log.d("CheckEmailUser: ", "checked success $fullname")
                        SharedPreference.getInastance(application.applicationContext).save_InfoData(fullname, email, pathType, _id)

                        responseMessage.postValue("success")
                         /*runBlocking {
                            delay(7000)
                            responseMessage.postValue("")
                         }*/
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("CheckEmailUser: ", "${error.message}")
            }
        })
    }

    private fun sentInfoToRealTimeDB(fname: String, email: String, pathType: String){

        val _id= randNumb(1220254, 263)
        var map = HashMap<String, Any>()

        map["full_name"] = fname
        map["email"] = email
        map["id"] = "$_id"

        reference.child(pathType).child("$_id").setValue(map)
                .addOnCompleteListener {
                    SharedPreference.getInastance(application.applicationContext)
                        .save_InfoData(fname,
                                email,
                                pathType,
                                "$_id")
                    Log.d("RegisterProcessTAG:  ", " = success")
                    responseMessage.postValue("success")
                }.addOnFailureListener { e->
                    responseMessage.postValue("Error ${e.message}")
                }

    }

    fun randNumb(max: Int, min: Int): Int {
        val rn = Random()
        val n = max - min + 1
        val i: Int = rn.nextInt() % n
        return min + i
    }
}