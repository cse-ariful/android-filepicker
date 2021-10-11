package com.nightcode.mediapicker.domain.viewModels.mediaList

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.nightcode.mediapicker.domain.usecases.GetVideosUseCase
import com.nightcode.mediapicker.frameworks.mediastore.LocalMediaRepositoryImpl
import java.lang.IllegalArgumentException

class MyViewModelFactory(private val mApplication: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaListViewModel::class.java)) {
            return MediaListViewModel(GetVideosUseCase(LocalMediaRepositoryImpl(mApplication))) as T;
        }
        throw IllegalArgumentException("Please provide the way how this viewModel can be initialized in MyViewModelFactory class")
    }
}