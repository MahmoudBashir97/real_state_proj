package com.mahmoudbashir.realstate.pojo

data class requestModel (
        val requestId:String?=null,
        val userId:String?=null,
        val ownerId:String?=null,
        val productId:String?=null,
        val productSort:String?=null,
        val productType:String?=null,
        val productPrice:String?=null,
        val categoryType:String?=null,
        val productDesc:String?=null,
        val address:String?=null,
        val product_imgs:List<String>?=null,
        val requestDate:String?=null,
        val services: Services?=null
        )