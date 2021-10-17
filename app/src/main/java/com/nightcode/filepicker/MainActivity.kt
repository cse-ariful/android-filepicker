package com.nightcode.filepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import com.nightcode.mediapicker.databinding.ActivityFilePickerBinding
import com.nightcode.mediapicker.domain.AppConstants
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.presentation.activity.MediaFilePickerActivity

class MainActivity : MediaFilePickerActivity() {
    override fun updateSelection(list: List<VideoModel>) {
        super.updateSelection(list)
    }

    override fun updateSelection(videoModel: VideoModel) {
        if (getSelectedFiles().value!!.firstOrNull { it.uri == videoModel.uri } == null && getSelectedFiles().value!!.size == 3) {
            PurchaseDialog().show(supportFragmentManager, "TAG")
            return
        }
        super.updateSelection(videoModel)
    }

    override fun onActivityResultIntercept(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        if (AppConstants.MEDIA_PICK_REQUEST_CODE == requestCode && Activity.RESULT_OK == resultCode && data != null && data.data != null) {

            val selectedImageUri: Uri = data.data!!
            when (val details = getVideoDetailsFromUriUseCase(selectedImageUri)) {
                is ResultData.Success -> {
                    updateSelection(details.data)
                    return true
                }
                is ResultData.Error -> {
                    val path = FFmpegKitConfig.getSafParameterForRead(this, selectedImageUri)
                    val details = FFprobeKit.getMediaInformation(path)
                    val duration = (details.mediaInformation.duration.toDouble() * 1000).toLong()
                    val length = (details.mediaInformation.size.toDouble() * 1000).toLong()
                    val title = details.mediaInformation.filename
                    val detils = VideoModel(
                        title,
                        selectedImageUri.toString(),
                        size = length,
                        duration = duration,
                        path = path
                    )
                    updateSelection(detils)
                    Log.d(TAG, "onActivityResultIntercept: ${detils}")
                }
            }
        } else {
            return false
        }
        return true
    }

    private fun executeTask(videoModel: VideoModel) {
        Log.d("testingTesting", "executing: ${videoModel.uri}")
        val path = FFmpegKitConfig.getSafParameterForRead(this, Uri.parse(videoModel.uri))
        Log.d("testingTesting", "executing:path ${path}")
        val session =
            FFmpegKit.execute("-hide_banner -i ${path} ${getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tttttout.mp4")
        Log.d("testingTesting", "executeTask: ${session.allLogsAsString}")
    }
}