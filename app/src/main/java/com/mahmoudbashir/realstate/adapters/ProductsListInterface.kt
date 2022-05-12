package com.mahmoudbashir.realstate.adapters

import com.mahmoudbashir.realstate.pojo.productModel

interface ProductsListInterface {
    fun productListResponse(mlist:MutableList<productModel>)
}