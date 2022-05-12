package com.mahmoudbashir.realstate.pojo

import java.io.Serializable

data class productModel(
        val ownerId:String?=null,
        val productId:String?=null,
        val productSort:String?=null,
        val productType:String?=null,
        val productPrice:String?=null,
        val categoryType:String?=null,
        val productDesc:String?=null,
        val address:String?=null,
        val product_imgs:List<String>?=null,
        var services: Services?=null
):Serializable
