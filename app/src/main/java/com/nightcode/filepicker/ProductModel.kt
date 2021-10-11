package com.nightcode.filepicker

import java.io.Serializable

data class ProductModel(
    val title: String,
    val price: String,
    val duration: String,
    val payload: String
) :Serializable{
}