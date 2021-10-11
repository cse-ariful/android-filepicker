package com.nightcode.mediapicker.domain.usecases

import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.FolderModel
import com.nightcode.mediapicker.data.repositories.LocalMediaRepository


class GetFoldersUserCase(private val localMediaRepository: LocalMediaRepository):
    AbstractUseCase<String?, ResultData<List<FolderModel>>> {
    override fun invoke(params: String?): ResultData<List<FolderModel>> {
         return localMediaRepository.getFolderNames()
    }
}