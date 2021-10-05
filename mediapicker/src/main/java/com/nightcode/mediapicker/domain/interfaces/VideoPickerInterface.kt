package com.nightcode.mediapicker.domain.interfaces

import androidx.lifecycle.LiveData
import com.nightcode.mediapicker.domain.entities.VideoModel

interface VideoPickerInterface {
    fun updateSelection(videoModel: VideoModel)
    fun getSelectedFiles(): LiveData<List<VideoModel>>
    fun onLongPressItem(imageFile: VideoModel): Boolean
    fun getSortMode(): SortMode
    fun getLayoutMode(): LayoutMode
//    fun bindCallback(callback:MediaPickerCallback)
//    fun unBindCallback(callback:MediaPickerCallback)
}

enum class LayoutMode {
    GRID,
    LIST
}

enum class SortMode {
    BY_TITLE_ASC,
    BY_TITLE_DESC,
    BY_SIZE_ASC,
    BY_SIZE_DESC
}