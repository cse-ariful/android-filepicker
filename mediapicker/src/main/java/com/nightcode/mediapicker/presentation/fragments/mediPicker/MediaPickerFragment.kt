package com.nightcode.mediapicker.presentation.fragments.mediPicker

import android.content.Context
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nightcode.mediapicker.BuildConfig
import com.nightcode.mediapicker.databinding.FragmentMediaPickerBinding
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.interfaces.LayoutMode
import com.nightcode.mediapicker.domain.interfaces.SortMode
import com.nightcode.mediapicker.domain.interfaces.VideoPickerInterface
import com.nightcode.mediapicker.presentation.fragments.browseMedia.BrowseMediaFragment
import com.nightcode.mediapicker.presentation.fragments.folderList.FragmentFolderList
import com.nightcode.mediapicker.presentation.fragments.mediaList.MediaListFragment
import org.arif.converter.fragments.BaseFragment
import java.lang.IllegalStateException

class MediaPickerFragment :
    BaseFragment<FragmentMediaPickerBinding>(FragmentMediaPickerBinding::inflate),
    VideoPickerInterface {

    var callback: VideoPickerInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when {
            parentFragment is VideoPickerInterface -> {
                callback = parentFragment as VideoPickerInterface
            }
            activity is VideoPickerInterface -> {
                callback = activity as VideoPickerInterface
            }
            BuildConfig.DEBUG -> {
                throw IllegalStateException("Please implement VideoPickerInterface in fragment or activity you are using MediaPickerFragment")
            }
        }
    }

    override fun initView() {
        initToolbar()
        initViewPager()
        initTabs()
    }

    private fun initToolbar() {

    }

    private fun initTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Media"
                1 -> "Folders"
                else -> "Browse"
            }
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { setStyleForTab(it, Typeface.BOLD) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let { setStyleForTab(it, Typeface.NORMAL) }
            }

            fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
                tab.view.children.find { it is TextView }?.let { tv ->
                    (tv as TextView).post {
                        tv.setTypeface(null, style)
                    }
                }
            }
        })

    }

    private fun initViewPager() {
        binding.viewPager2.run {
            adapter = object : FragmentStateAdapter(requireActivity()) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> MediaListFragment()
                        1 -> FragmentFolderList()
                        else -> BrowseMediaFragment()
                    }
                }
            }
            offscreenPageLimit = 3
        }

    }

    override fun updateSelection(videoModel: VideoModel) {
        callback?.updateSelection(videoModel)

    }

    @Suppress("UNCHECKED_CAST")
    override fun getSelectedFiles(): LiveData<List<VideoModel>> =
        callback?.getSelectedFiles() ?: MutableLiveData()

    override fun onLongPressItem(imageFile: VideoModel): Boolean {
        return callback?.onLongPressItem(imageFile) ?: false
    }

    override fun getSortMode(): SortMode {
        return callback?.getSortMode() ?: SortMode.BY_TITLE_ASC
    }

    override fun getLayoutMode(): LayoutMode {
        return callback?.getLayoutMode() ?: LayoutMode.GRID
    }


}