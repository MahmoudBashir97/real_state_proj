package com.mahmoudbashir.realstate.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.ImagesList
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.requestModel
import com.mahmoudbashir.realstate.repository.Repository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashMap

class RealStateviewModel(val app:Application,val repo:Repository) : AndroidViewModel(app) {

    lateinit var myStorageRef: StorageReference
    lateinit var fileStorageReference: StorageReference
    var productsList : MutableLiveData<List<productModel>> = MutableLiveData()
    var productsRef:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Products")
    var imgsList: MutableList<Uri> = ArrayList<Uri>()
    var imgsListString: MutableList<String> = ArrayList()


    fun registerLiveData(fullName: String, email: String, password: String,pathType:String)= viewModelScope.launch {
        repo.RegisterNewAccountToFirebase(fullName,email,password,pathType)
    }

    fun checkRegisteration():LiveData<String>{
        return  repo.responseMessage
    }

    fun registerLiveData( email: String, password: String,pathType: String)= viewModelScope.launch {
        repo.LoginWithFirebase(email,password,pathType)
    }

    fun uploadProduct(productModel: productModel)=viewModelScope.launch {
        repo.uploadNewProduct(productModel)
    }

    fun getResponseMessage():LiveData<String>{
       return repo.responseMessage
    }

    fun getProductList() = viewModelScope.launch {
        repo.getProductList()
    }

    fun getProductsBySort(): MutableList<productModel> {
        return repo.mlist
    }

    fun uploadNewRequest(productModel: productModel)=viewModelScope.launch {
         repo.addNewRequest(productModel)
    }

    fun getRequests()=viewModelScope.launch {
        repo.getRequests()
    }

    fun getRequestsList(): MutableList<requestModel> {
        return repo.requestlist
    }

    fun uploadProducts(prodId:String,imgsList: MutableList<Uri>){
        myStorageRef = FirebaseStorage.getInstance().reference//.child("Images")

        for (elem in imgsList){
            uploadImagesToFirebaseStorage(elem,prodId)
        }

    }
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = app.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImagesToFirebaseStorage(imageuri: Uri,prodId:String) {

        val s = System.currentTimeMillis().toString() + randNumb(24516499, 150)
        fileStorageReference = myStorageRef.child("Images").child(
            s+
                    "." + getFileExtension(imageuri)
        )

        val path = "https://firebasestorage.googleapis.com/v0/b/realstateproj.appspot.com/o/Images%2F${s}.${getFileExtension(imageuri)}?alt=media&token=45fe63e2-aa9e-4005-91c8-426e5db5d725"
        Log.d("pathF : ", path)
        imgsListString.add(path)
        var map: HashMap<String,Any> = HashMap()
        fileStorageReference.putFile(imageuri)
            .addOnSuccessListener {
                if (it.task.isSuccessful){
                    Log.d("pathF : ", "after success ${fileStorageReference.path}")
                    //val imgList = ImagesList(imgsListString)
                    map["product_imgs"] = imgsList
                    productsRef.child(prodId)
                        .updateChildren(map)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d("uploadingViewModel : ","success")
                            }
                        }.addOnFailureListener {e->
                            e.message
                        }
                }
            }.addOnFailureListener { e ->
                e.message
                // showErrorMessage("Some Error occurs!!")
            }
    }

    fun randNumb(max: Int, min: Int): Int {
        val rn = Random()
        val n = max - min + 1
        val i: Int = rn.nextInt() % n
        var result = min + i

        if (result < 0) result *= -1

        return result
    }

}