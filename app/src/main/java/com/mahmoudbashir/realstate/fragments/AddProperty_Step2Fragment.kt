package com.mahmoudbashir.realstate.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.kaopiz.kprogresshud.KProgressHUD
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.Utils.Constants
import com.mahmoudbashir.realstate.adapters.gridImageAdapter
import com.mahmoudbashir.realstate.databinding.FragmentAddPropertyStep2Binding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.ImagesList
import com.mahmoudbashir.realstate.pojo.Services
import com.mahmoudbashir.realstate.pojo.productModel
import com.mahmoudbashir.realstate.pojo.step1Model
import com.mahmoudbashir.realstate.ui.MainActivity
import com.mahmoudbashir.realstate.viewModel.RealStateviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class AddProperty_Step2Fragment : Fragment(), gridImageAdapter.RemoveItemInterface {


    private lateinit var step2Binding: FragmentAddPropertyStep2Binding
    lateinit var gridAdapter: gridImageAdapter
    var imgUri: Uri? = null
    lateinit var viewModel: RealStateviewModel

    var imgsList: MutableList<Uri> = ArrayList<Uri>()
    var imgsListString: MutableList<String> = ArrayList()
    var imagesPath: MutableList<String> = ArrayList()
    lateinit var step1Model: step1Model
    lateinit var myStorageRef: StorageReference
    lateinit var fileStorageReference: StorageReference
    lateinit var productsRef: DatabaseReference
    lateinit var imgList: ImagesList
    var prodId = ""
    var isNavigated = false
    var isAllImgsUploaded = false
    var userType:String?=""

    lateinit var dialog: KProgressHUD


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        step2Binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_property__step2,
                container,
                false
        )
        step1Model = arguments?.getSerializable("step1model") as step1Model
        // initialize ViewModel
        viewModel = (activity as MainActivity).viewModel
        userType = SharedPreference.getInastance(context).userType

        myStorageRef = FirebaseStorage.getInstance().reference//.child("Images")
        initializeProgressDialog()


        return step2Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateUpToPrevious()
        uploadImgButtonClicked()
        setUpGridView()
        submitClicked()

        productsRef = FirebaseDatabase.getInstance().reference.child("Products")

    }


    private fun initializeProgressDialog() {
        dialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploading your product...")
                .setCancellable(false)
                .setBackgroundColor(requireContext().resources.getColor(R.color.primary_dark))
                .setCornerRadius(8f)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
    }

    private fun submitClicked() {
        var count = 0
        step2Binding.submitBtn.setOnClickListener {
            if (validate()) {
                Constants.hideKeyboard(activity as MainActivity)
                dialog.show()
                prodId = randNumb(9999999, 99).toString()
                for (elem in imgsList) {
                    runBlocking {
                        delay(200)
                        uploadImagesToFirebaseStorage(elem)
                        Log.d("pathF : ", " ${count++}")
                    }
                }
               /* runBlocking {
                    delay(500)
                    Log.d("pathF : ", "before $isAllImgsUploaded , ${imagesPath.size}")
                    if (isAllImgsUploaded && imagesPath.size > 0) {
                        Log.d("pathF : ", "uploaded success!")
                    }
                }*/
            }
        }
    }

    private fun uploadImagesToFirebaseStorage(imageuri: Uri) {
        val s = System.currentTimeMillis().toString() + randNumb(24516499, 150)
        fileStorageReference = myStorageRef.child("Images").child(
                s+
                        "." + getFileExtension(imageuri)
        )

        val path = "https://firebasestorage.googleapis.com/v0/b/realstateproj.appspot.com/o/Images%2F${s}.${getFileExtension(imageuri)}?alt=media&token=45fe63e2-aa9e-4005-91c8-426e5db5d725"
        Log.d("pathF : ", path)
        imgsListString.add(path)
        fileStorageReference.putFile(imageuri)
                .addOnSuccessListener {
                    if (it.task.isComplete){
                         isAllImgsUploaded = true
                      Log.d("pathF : ", "after success $isAllImgsUploaded , ${fileStorageReference.path}")
                      if (isAllImgsUploaded && imgsListString.size > 0) {
                        Log.d("pathF : ", "uploaded success!")

                        runBlocking {
                            val services = Services(false,false,false)
                            val model = productModel(
                                    SharedPreference.getInastance(context).userId,
                                    prodId,
                                    step1Model.sort,
                                    step1Model.propertyType,
                                    step1Model.price,
                                    step1Model.categoryType,
                                    step1Model.description,
                                    step2Binding.edtAddress.text.toString(),
                                    imgsListString,
                                    services
                            )
                            uploadNewProduct(model)
                        }
                      }
                    }
                }.addOnFailureListener { e ->
                    e.message
                    isAllImgsUploaded = false
                    dialog.dismiss()
                   // showErrorMessage("Some Error occurs!!")
                }
    }

    private fun uploadNewProduct(productModel: productModel) {

        productsRef.child(productModel.productId!!)
                .setValue(productModel)
                .addOnCompleteListener {
                    if (it.isComplete) {
                        dialog.dismiss()
                       /* findNavController().popBackStack(
                            findNavController().currentBackStackEntry?.destination?.id!!,
                            true
                        )*/

                        //findNavController().popBackStack(R.id.homeFragment,true)
                        navigateUpToHome()
                        Log.d("listImg : ", "${productModel.product_imgs!!.size} uploadedddd")
                    }
                }.addOnFailureListener {
                    it.message
                    dialog.dismiss()
                   showErrorMessage("Some Error occurs!!")
                }
    }

    private fun navigateUpToHome() {
        try {
            findNavController().navigate(AddProperty_Step2FragmentDirections.actionAddPropertyStep2FragmentToHomeFragment(
                userType!!,
                "property"
            ))
        }catch (ex:Exception){
            ex.message
        }

    }


    private fun validate(): Boolean {
        if (TextUtils.isEmpty(step2Binding.edtAddress.text.toString())) {
            step2Binding.edtAddress.error = "Please Fill this required field as your detailed address "
            step2Binding.edtAddress.requestFocus()
            return false
        } else if (imgsList.size <= 0) {
            (activity as MainActivity).showErrorMessage("You have to upload at least one image!")
            //showErrorMessage()
            return false
        } else return true
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText((activity as MainActivity).applicationContext, message, Toast.LENGTH_LONG).apply {
            show()
        }
    }

    private fun uploadImgButtonClicked() {
        step2Binding.relUploadImgs.setOnClickListener {
            openImage()
        }
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Constants.IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireActivity().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun navigateUpToPrevious() {
        step2Binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpGridView() {
        gridAdapter = gridImageAdapter(imgsList, this)
        step2Binding.gridV.apply {
            adapter = gridAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.data != null) {
            imgUri = data.data
            imgsList.add(imgUri!!)

            gridAdapter.notifyDataSetChanged()
        }
    }

    override fun onRemoveItemClicked(position: Int) {
        imgsList.removeAt(position)
        setUpGridView()
        gridAdapter.notifyDataSetChanged()
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