package com.nightcode.mediapicker.domain.entities

data class VideoModel(
    val title: String,
    val uri: String,
    val size: Long,
    val duration: Long,
    val path: String,//full path or relative path if greater than android 10
    var selected: Boolean = false
) : MediaModel()