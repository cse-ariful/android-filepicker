package com.nightcode.mediapicker.domain.viewModels.mediaList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.interfaces.LayoutMode
import com.nightcode.mediapicker.domain.interfaces.SortMode
import com.nightcode.mediapicker.domain.usecases.GetVideosUseCase
import kotlinx.coroutines.launch

class MediaListViewModel constructor(private val getVideosUseCase: GetVideosUseCase) : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    val files = MutableLiveData<List<VideoModel>?>()
    private var folderName: String? = null
    val layoutMode = MutableLiveData(LayoutMode.GRID)
    val sortMode = MutableLiveData(SortMode.BY_TITLE_ASC)

    init {
        //refresh()
    }

    fun refresh() {
        if (loading.value == true) return
        loading.postValue(true)
        viewModelScope.launch {
            val medias = getVideosUseCase(folderName)
            if (medias is ResultData.Success) {
                files.postValue(medias.data)
            }
            loading.postValue(false)
        }
    }

    fun setFolderName(string: String?) {
        folderName = string
        refresh()
    }
}