package com.nightcode.filepicker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import com.nightcode.mediapicker.databinding.ActivityFilePickerBinding
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

    private fun executeTask(videoModel: VideoModel) {
        Log.d("testingTesting", "executing: ${videoModel.uri}")
        val path = FFmpegKitConfig.getSafParameterForRead(this, Uri.parse(videoModel.uri))
        Log.d("testingTesting", "executing:path ${path}")
        val session = FFmpegKit.execute("-hide_banner -i ${path} ${getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tttttout.mp4")
        Log.d("testingTesting", "executeTask: ${session.allLogsAsString}")
    }
}