package com.nightcode.mediapicker.data.repositories

import android.net.Uri
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.FolderModel
import com.nightcode.mediapicker.domain.entities.VideoModel


interface LocalMediaRepository {
    fun getAllVideos(parentFolder: String?): ResultData<List<VideoModel>>
    fun getFolderNames(): ResultData<List<FolderModel>>
    fun getVideoDetails(uri: Uri): VideoModel
}