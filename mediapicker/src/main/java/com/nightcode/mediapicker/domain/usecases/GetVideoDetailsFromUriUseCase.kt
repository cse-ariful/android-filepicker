package com.nightcode.mediapicker.domain.usecases

import android.net.Uri
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.data.repositories.LocalMediaRepository
import java.lang.Exception

class GetVideoDetailsFromUriUseCase(private val localMediaRepository: LocalMediaRepository) :
    AbstractUseCase<Uri, ResultData<VideoModel>> {
    override fun invoke(params: Uri): ResultData<VideoModel> {
        return try {
            ResultData.Success(localMediaRepository.getVideoDetails(uri = params))
        } catch (ex: Exception) {
            ResultData.Error(throwable = ex, message = ex.message)
        }
    }
}