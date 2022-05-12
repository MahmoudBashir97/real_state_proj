package com.mahmoudbashir.realstate.pojo

import java.io.Serializable

data class ItemModel (
    val ownerId:String?="",
    val itemId:String?="",
    val itemName:String?="",
    val itemPrice:String?="",
    val itemDesc:String?="",
    val itemImgPath:String?=""
    ): Serializable