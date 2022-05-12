package com.mahmoudbashir.realstate.pojo

import android.os.Parcelable
import java.io.Serializable

data class step1Model(
    val sort:String,
    val propertyType:String,
    val categoryType:String,
    val description:String,
    val price:String
):Serializable
