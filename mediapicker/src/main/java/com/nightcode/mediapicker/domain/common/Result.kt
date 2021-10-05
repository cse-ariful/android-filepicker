package com.nightcode.mediapicker.domain.common

/**
 * this is a generic class that can hold all type of data and states regarding them
 * if state is success then this will contain the target data type
 * else it will contain a data relative to the type that it is
 * like for error state it will contain a message
 */
sealed class ResultData<out T>{
    data class Success<out T>(val data:T): ResultData<T>()
    data class Error(val throwable:Throwable?,val message:String?): ResultData<Nothing>()
}