package com.mahmoudbashir.realstate.repository

import com.mahmoudbashir.realstate.pojo.productModel

interface IRepository {

    suspend fun RegisterNewAccountToFirebase(fullName:String,email:String,password:String,pathType:String)
    suspend fun LoginWithFirebase(email:String,password:String,pathType: String)
    suspend fun uploadNewProduct(productModel: productModel)
    suspend fun getProductList()
    suspend fun addNewRequest(model:productModel)
    suspend fun getRequests()

}