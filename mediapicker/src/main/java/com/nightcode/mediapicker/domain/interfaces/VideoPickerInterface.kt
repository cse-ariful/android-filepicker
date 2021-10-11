package com.nightcode.mediapicker.domain.interfaces

import androidx.lifecycle.LiveData
import com.nightcode.mediapicker.domain.constants.LayoutMode
import com.nightcode.mediapicker.domain.constants.SortMode
import com.nightcode.mediapicker.domain.entities.VideoModel

interface VideoPickerInterface {
    fun updateSelection(videoModel: VideoModel)
    fun updateSelection(list: List<VideoModel>)
    fun getSelectedFiles(): LiveData<List<VideoModel>>
    fun onLongPressItem(imageFile: VideoModel): Boolean
    fun getSortMode(): SortMode
    fun getLayoutMode(): LayoutMode
//    fun bindCallback(callback:MediaPickerCallback)
//    fun unBindCallback(callback:MediaPickerCallback)
}

