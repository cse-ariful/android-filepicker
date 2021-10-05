package com.nightcode.mediapicker.domain.entities

data class FolderModel(val title: String,val thumb:String, val fileCount: Int, val totalSize: Long) : MediaModel()