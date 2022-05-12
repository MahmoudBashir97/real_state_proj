package com.mahmoudbashir.realstate.pojo

import java.io.Serializable

data class Services(
        val Plumber:Boolean?=null,
        val Carpenter:Boolean?=null,
        val Electrician:Boolean?=null
): Serializable
