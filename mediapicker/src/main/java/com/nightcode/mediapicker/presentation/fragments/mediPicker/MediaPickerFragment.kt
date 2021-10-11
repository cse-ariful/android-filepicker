package com.nightcode.mediapicker.presentation.fragments.mediPicker

import android.content.Context
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nightcode.mediapicker.BuildConfig
import com.nightcode.mediapicker.databinding.FragmentMediaPickerBinding
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.constants.LayoutMode
import com.nightcode.mediapicker.domain.interfaces.MediaFragment
import com.nightcode.mediapicker.domain.constants.SortMode
import com.nightcode.mediapicker.domain.interfaces.VideoPickerInterface
import com.nightcode.mediapicker.presentation.ViewExtension.showIf
import com.nightcode.mediapicker.presentation.fragments.browseMedia.BrowseMediaFragment
import com.nightcode.mediapicker.presentation.fragments.folderList.FragmentFolderList
import com.nightcode.mediapicker.presentation.fragments.mediaList.MediaListFragment
import org.arif.converter.fragments.BaseFragment
import java.lang.IllegalStateException

class MediaPickerFragment :
    BaseFragment<FragmentMediaPickerBinding>(FragmentMediaPickerBinding::inflate),
    VideoPickerInterface {

    var callback: VideoPickerInterface? = null
    var initOnAttached = false
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
        if (initOnAttached) {
            Toast.makeText(context, "Initializing from onAttach", Toast.LENGTH_SHORT).show()
            initView()
        }
    }

    override fun initView() {
        if (!isAdded) {
            initOnAttached = true
            return
        }
        initOnAttached = false
        initToolbar()
        initViewPager()
        initTabs()
    }

    private fun initToolbar() {
        binding.selectAll.setOnClickListener {
            getCurrentFragment()?.toggleSelectAll()
        }
    }

    private fun getCurrentFragment(): MediaFragment? {
        return when (binding.viewPager2.currentItem) {
            0 -> mediaListFragment as? MediaFragment?
            1 -> folderListFragment as? MediaFragment?
            else -> null
        }
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

    var mediaListFragment: MediaListFragment? = null
    var folderListFragment: FragmentFolderList? = null
    private fun initViewPager() {

        binding.viewPager2.run {
            adapter = object : FragmentStateAdapter(requireActivity()) {
                override fun getItemCount(): Int = 3
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> {
                            mediaListFragment = mediaListFragment ?: MediaListFragment()
                            return mediaListFragment!!
                        }
                        1 -> {
                            folderListFragment = folderListFragment ?: FragmentFolderList()
                            return folderListFragment!!
                        }
                        else -> BrowseMediaFragment()
                    }
                }
            }
            offscreenPageLimit = 3
        }
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.selectAll.showIf(position < 2)
            }
        })

    }

    override fun updateSelection(videoModel: VideoModel) {
        callback?.updateSelection(videoModel)

    }

    override fun updateSelection(list: List<VideoModel>) {
        callback?.updateSelection(list)
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

    fun onBackPressed(): Boolean {
        if (binding.viewPager2.currentItem == 1) {
            val handle = getCurrentFragment()?.handleBackPress() ?: false
            if (handle) return true
            binding.viewPager2.currentItem = 0
            return true
        } else if (binding.viewPager2.currentItem > 0) {
            binding.viewPager2.currentItem = 0
            return true
        }
        return false
    }


}