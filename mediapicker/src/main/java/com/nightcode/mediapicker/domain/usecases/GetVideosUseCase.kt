package com.nightcode.mediapicker.domain.usecases

import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.data.repositories.LocalMediaRepository

class GetVideosUseCase(private val localMediaRepository: LocalMediaRepository) :
    AbstractUseCase<String?, ResultData<List<VideoModel>>> {
    override fun invoke(params: String?): ResultData<List<VideoModel>> {
        return localMediaRepository.getAllVideos(params)
    }
}