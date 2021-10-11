package com.nightcode.mediapicker.presentation.activity

import android.content.Intent
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.nightcode.mediapicker.R
import com.nightcode.mediapicker.databinding.ActivityFilePickerBinding
import com.nightcode.mediapicker.databinding.ItemSelectedFilesThumbBinding
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.interfaces.VideoPickerInterface
import com.nightcode.mediapicker.presentation.ViewExtension.asVisibility
import com.nightcode.mediapicker.domain.adapters.AbstractAdapter
import com.nightcode.mediapicker.domain.constants.LayoutMode
import com.nightcode.mediapicker.domain.constants.SortMode
import com.nightcode.mediapicker.presentation.fragments.mediPicker.MediaPickerFragment
import org.greenrobot.eventbus.EventBus
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.nightcode.mediapicker.frameworks.mediastore.LocalMediaRepositoryImpl
import com.nightcode.mediapicker.domain.AppConstants
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.usecases.GetVideoDetailsFromUriUseCase
import com.nightcode.mediapicker.presentation.fragments.mediaList.MediaListFragment
import com.nightcode.mediapicker.presentation.fragments.search.SearchFragment


open class MediaFilePickerActivity :
    BaseActivity<ActivityFilePickerBinding>(ActivityFilePickerBinding::inflate),
    VideoPickerInterface {
    companion object {
        private const val TAG = "FilePickerActivity"
    }

    private val selectedFiles = MutableLiveData<MutableList<VideoModel>>(mutableListOf())
    val getVideoDetailsFromUriUseCase by lazy {
        GetVideoDetailsFromUriUseCase(
            LocalMediaRepositoryImpl(this)
        )
    }
    private val adapter =
        object :
            AbstractAdapter<VideoModel, ItemSelectedFilesThumbBinding>(ItemSelectedFilesThumbBinding::inflate) {
            override fun bind(binding: ItemSelectedFilesThumbBinding, item: VideoModel) {
                Glide.with(binding.thumb)
                    .load(item.uri)
                    .placeholder(R.drawable.video_placeholder)
                    .into(binding.thumb)
                binding.remove.setOnClickListener {
                    updateSelection(item)
                }
            }

        }

    var mediaPickerFragment: MediaPickerFragment? = null
    var isGridMode = true


    override fun initView() {
        mediaPickerFragment = MediaPickerFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, mediaPickerFragment!!)
            .commitAllowingStateLoss()
        initToolbar()
        initSelectionController()
    }

    private fun initSelectionController() {
        binding.selectedFilesList.adapter = adapter
        selectedFiles.observe(this) {
            binding.selectionController.visibility = it.isNotEmpty().asVisibility()
            binding.count.text = it.size.toString()
            adapter.submitList(it)
            if (adapter.itemCount > 0)
                binding.selectedFilesList.smoothScrollToPosition(
                    (adapter.itemCount - 1).coerceAtLeast(
                        0
                    )
                )
        }
        binding.clearBtn.setOnClickListener {
            selectedFiles.postValue(mutableListOf())
        }
        binding.nextBtn.setOnClickListener {
            if (selectedFiles.value!!.size > 0) {
                // FileHolder.setFiles(selectedFiles.value!!)
                // startActivity(Intent(this, VideoConverterScreen::class.java))
                //startActivity(Intent(this, PlayerActivity::class.java))
            }
        }
    }

    var menu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.file_picker_menu, menu)
        this.menu = menu
        return true
    }


    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()

        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_toggle_mode -> {
                    isGridMode = !isGridMode
                    if (isGridMode) {
                        EventBus.getDefault().post(LayoutMode.GRID)
                        item.icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_list_mode)
                    } else {
                        EventBus.getDefault().post(LayoutMode.LIST)
                        item.icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_grid_mode)
                    }
                    notifyLayouType(isGridMode)
                }
                R.id.action_sort -> {

                }
                R.id.action_search -> {
                    val ft = supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_up_low,
                            R.anim.slide_down_low,
                            R.anim.slide_up_low,
                            R.anim.slide_down_low
                        )
                        .replace(binding.searchViewContainer.id, SearchFragment(), "TAG")
                    ft.addToBackStack("TAG")
                    ft.commitAllowingStateLoss()

                }
            }
            binding.toolbar.invalidate()
            return@setOnMenuItemClickListener true
        }
    }

    private fun notifyLayouType(gridMode: Boolean) {

    }

    override fun updateSelection(videoModel: VideoModel) {
        val currentItems = selectedFiles.value!!
        val index = currentItems.indexOf(videoModel)
        if (index == -1) {
            currentItems.add(videoModel)
        } else {
            currentItems.removeAt(index)
        }
        selectedFiles.postValue(currentItems)

    }

    override fun updateSelection(list: List<VideoModel>) {
        Log.d(TAG, "updateSelection: ${list.size}")

        val count = selectedFiles.value!!.size
        val newList = selectedFiles.value!!.union(list).distinctBy { it.uri }.toMutableList()

        Log.d(TAG, "updateSelection: ${newList.size}")
        if (newList.size == count) {
            selectedFiles.postValue(mutableListOf())
        } else {
            selectedFiles.postValue(newList)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getSelectedFiles(): LiveData<List<VideoModel>> =
        selectedFiles as LiveData<List<VideoModel>>

    override fun onLongPressItem(imageFile: VideoModel): Boolean {
        return true
    }

    override fun getSortMode(): SortMode {
        return SortMode.BY_TITLE_ASC
    }

    override fun getLayoutMode(): LayoutMode {
        return LayoutMode.GRID
    }

    override fun onBackPressed() {
        if ((mediaPickerFragment?.onBackPressed() == true)) {
            return;
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (AppConstants.MEDIA_PICK_REQUEST_CODE == requestCode && Activity.RESULT_OK == resultCode && data != null && data.data != null) {

            val selectedImageUri: Uri = data.data!!
            when (val details = getVideoDetailsFromUriUseCase(selectedImageUri)) {
                is ResultData.Success -> {
                    updateSelection(details.data)
                }
                is ResultData.Error -> {
                    details.throwable?.printStackTrace()
                    Toast.makeText(this, "Error getting video details", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getViewObject(): ActivityFilePickerBinding {
        return binding
    }


}

