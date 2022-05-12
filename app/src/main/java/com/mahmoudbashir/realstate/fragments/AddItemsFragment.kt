package com.mahmoudbashir.realstate.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kaopiz.kprogresshud.KProgressHUD
import com.mahmoudbashir.realstate.R
import com.mahmoudbashir.realstate.Utils.Constants
import com.mahmoudbashir.realstate.databinding.FragmentAddItemsBinding
import com.mahmoudbashir.realstate.local.SharedPreference
import com.mahmoudbashir.realstate.pojo.ItemModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import java.util.*

class AddItemsFragment : Fragment() {

    lateinit var addItemsBinding: FragmentAddItemsBinding
    var imgUri: Uri? = null
    lateinit var myStorageRef: StorageReference
    lateinit var fileStorageReference: StorageReference
    lateinit var productsRef: DatabaseReference
    lateinit var itemRef:DatabaseReference
    lateinit var dialog: KProgressHUD


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addItemsBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_add_items, container, false)
        myStorageRef = FirebaseStorage.getInstance().reference


        return addItemsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemRef = FirebaseDatabase.getInstance().reference

        backPressedbtn()
        submitClicked()
        uploadImgClicked()
        initializeProgressDialog()

    }

    private fun backPressedbtn() {
        addItemsBinding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun uploadImgClicked() {
        addItemsBinding.relUploadImgs.setOnClickListener {
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

    private fun submitClicked() {
        addItemsBinding.submitBtn.setOnClickListener {
            dialog.show()
            if (validateData()){
                uploadImagesToFirebaseStorage(imgUri!!)
            }else dialog.dismiss()
        }
    }

    private fun uploadImagesToFirebaseStorage(imageuri: Uri) {
        val s = System.currentTimeMillis().toString() + randNumb(24516499, 150)
        fileStorageReference = myStorageRef.child("Items").child(
            s+"." + getFileExtension(imageuri))

        val path = "https://firebasestorage.googleapis.com/v0/b/realstateproj.appspot.com/o/Items%2F${s}.${getFileExtension(imageuri)}?alt=media&token=45fe63e2-aa9e-4005-91c8-426e5db5d725"

        fileStorageReference.putFile(imageuri)
            .addOnSuccessListener {
                it->
                if (it.task.isComplete){
                    Log.d("pathF : ", "after success : , $path")
                    runBlocking {
                        val model = ItemModel(
                            SharedPreference.getInastance(context).userId,
                            "${randNumb(520,542520)}",
                            addItemsBinding.edtName.text.toString(),
                            addItemsBinding.edtPrice.text.toString(),
                            addItemsBinding.edtDesc.text.toString(),
                            path
                        )
                        uploadNewItem(model)
                    }
                }
            }.addOnFailureListener {
                e->
                e.message
                dialog.dismiss()
                showErrorMessage("some error occur")
            }
    }

    private fun uploadNewItem(model: ItemModel) {
        itemRef.child("Items")
            .child(model.itemId!!)
            .setValue(model)
            .addOnCompleteListener {
                it->
                if (it.isComplete){
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
            }.addOnFailureListener {
                e->
                e.message
                showErrorMessage("some error occur")
                dialog.dismiss()
            }
    }

    private fun validateData():Boolean{

        return if (TextUtils.isEmpty(addItemsBinding.edtName.text.toString())){
            addItemsBinding.edtName.error = "Please Enter an item name."
            addItemsBinding.edtName.requestFocus()
            false
        }else if (TextUtils.isEmpty(addItemsBinding.edtPrice.text.toString())){
            addItemsBinding.edtPrice.error = "Please Enter a price for your item."
            addItemsBinding.edtPrice.requestFocus()
            false
        }else if (TextUtils.isEmpty(addItemsBinding.edtDesc.text.toString())){
            addItemsBinding.edtDesc.error = "Please Enter at least one line description."
            addItemsBinding.edtDesc.requestFocus()
            false
        }else if (imgUri == null){
            showErrorMessage("please upload an image!")
            false
        }else true
    }

    private fun showErrorMessage(message:String){
        /*Snackbar.make(v,message,3000).apply {
            setBackgroundTint(Color.RED)
        }*/
        Toast.makeText(context,message, Toast.LENGTH_LONG).apply {
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null) {
            imgUri = data.data
            Picasso.get().load(imgUri).into(addItemsBinding.imgV)
        }
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


    fun randNumb(max: Int, min: Int): Int {
        val rn = Random()
        val n = max - min + 1
        val i: Int = rn.nextInt() % n
        var result = min + i

        if (result < 0) result *= -1

        return result
    }
}