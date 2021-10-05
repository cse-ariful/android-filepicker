package com.nightcode.mediapicker.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.FolderModel
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.repository.LocalMediaRepository
import java.lang.IllegalArgumentException
import java.util.*

class LocalMediaRepositoryImpl constructor(private val context: Context) : LocalMediaRepository {
    override fun getAllVideos(parentFolder: String?): ResultData<List<VideoModel>> {
        return ResultData.Success(getAllVideoByFolder(parentFolder))
    }

    private val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.WIDTH,
        MediaStore.Video.Media.HEIGHT,
        MediaStore.Video.Media.SIZE,
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            MediaStore.Video.Media.DATA
        else
            MediaStore.Video.Media.RELATIVE_PATH,
        MediaStore.Video.VideoColumns.DURATION,
        MediaStore.Video.Media.DATE_MODIFIED
    )

    override fun getFolderNames(): ResultData<List<FolderModel>> {
        val videos = getAllVideoByFolder(null)
        val sizeMap = HashMap<String, Long>()
        val countMap = HashMap<String, Int>()
        val thumbMap = HashMap<String, String>()
        val folders = mutableListOf<String>()
        videos.forEach {
            val folderName = getFolderName(it.path) ?: return@forEach
            folders.add(folderName)
            sizeMap[folderName] = (sizeMap[folderName] ?: 0) + it.size
            countMap[folderName] = (countMap[folderName] ?: 0) + 1
            thumbMap[folderName] = thumbMap[folderName] ?: it.uri
        }

        return ResultData.Success(
            folders.distinct()
                .map { FolderModel(it, thumbMap[it] ?: "", countMap[it] ?: 0, sizeMap[it] ?: 0) }
                .toList()
        )
    }

    override fun getVideoDetails(uri: Uri): VideoModel {
        var mediaCursor: Cursor? = null
        //  if (folderName == null || folderName.isEmpty()) {
        mediaCursor = context.contentResolver
            .query(
                uri,
                projection,
                null,
                null,
                null
            )
        mediaCursor?.moveToNext()
        if (mediaCursor == null) throw IllegalArgumentException("This uri data is not available at mediaStore")
        return readValueAtCursor(mediaCursor)!!
    }

    private fun getFolderName(path: String): String? {
        val subs = path.split("/")
        if (subs.size > 2) return subs[subs.size - 2]
        return null
    }


    companion object {
        private const val TAG = "LocalMediaDataSource"
        const val SUPPORTED_FORMAT = ".*"

        // "jpg|JPG|jpeg|JPEG|png|PNG|webp|WEBP|heic|HEIC|cr2|CR2|jp2|JP2|dng|DNG|nef,|NEF|arw|ARW"
        const val FILTER_QUERY =
            "${MediaStore.Images.Media.DATA} REGEXP '.*[\\/](?i)(%s)[\\/].*\$' and ${MediaStore.Video.Media.DATA} REGEXP '(?i).+\\.($SUPPORTED_FORMAT)'"
    }

    private fun getAllVideoByFolder(folderName: String? = null): ArrayList<VideoModel> {
        try {
            val images = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            var mediaCursor: Cursor? = null
            //  if (folderName == null || folderName.isEmpty()) {
            mediaCursor = context.contentResolver
                .query(
                    images,
                    projection,
                    String.format(Locale.US, FILTER_QUERY, folderName ?: ".*"),
                    null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC"
                )
            /* }else {
                     mediaCursor = context.contentResolver.query(
                         images,
                         projection,
                         MediaStore.Images.Media.DATA + " like ? ",
                         arrayOf("%$folderName%"),
                         MediaStore.Images.Media.DATE_MODIFIED + " DESC"
                     )
                 }*/
            Log.d(TAG, "getAllPhotosByFolder: ${mediaCursor?.count}")
            mediaCursor.let {
                return parseCursor(it).apply {
                    it?.close()
                }
            }

        } catch (ex: Exception) {
            Log.d(TAG, "getAllPhotosByFolder:ex $ex")
            return arrayListOf<VideoModel>()
        }
    }


    private fun parseCursor(cursor: Cursor?): ArrayList<VideoModel> {
        val array = arrayListOf<VideoModel>()
        cursor?.let { c ->
            while (c.moveToNext()) {
                readValueAtCursor(cursor).let { it ->
                    if (it != null)
                        array.add(it)

                }
            }
        }
        return array
    }

    private fun readValueAtCursor(cursor: Cursor): VideoModel? {
        val id = cursor.getInt(0)
        val title = cursor.getStringOrNull(1)
        val width = cursor.getStringOrNull(2)
        val height = cursor.getStringOrNull(3)
        val size = cursor.getLongOrNull(4)
        val duration = cursor.getLongOrNull(6)
        var path: String? = null
        try {
            path = cursor.getString(5)
        } catch (ex: Exception) {

        }

        val uri = ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            id.toLong()
        )
        return VideoModel(
            title = title!!,
            uri = uri.toString(),
            size = size ?: -1L,
            duration = duration ?: -1L,
            path = path!!
        )
    }


}