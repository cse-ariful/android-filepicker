package com.nightcode.mediapicker.presentation.fragments.folderList

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.nightcode.mediapicker.R
import com.nightcode.mediapicker.frameworks.mediastore.LocalMediaRepositoryImpl
import com.nightcode.mediapicker.databinding.FragmentFolderListBinding
import com.nightcode.mediapicker.databinding.ItemFolderListBinding
import com.nightcode.mediapicker.domain.common.ResultData
import com.nightcode.mediapicker.domain.entities.FolderModel
import com.nightcode.mediapicker.domain.usecases.GetFoldersUserCase
import com.nightcode.mediapicker.presentation.ViewExtension.load
import com.nightcode.mediapicker.presentation.ViewExtension.toReadableSize
import com.nightcode.mediapicker.presentation.fragments.mediaList.MediaListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.nightcode.mediapicker.domain.adapters.AbstractAdapter
import com.nightcode.mediapicker.domain.interfaces.MediaFragment
import org.arif.converter.fragments.BaseFragment

class FragmentFolderList :
    BaseFragment<FragmentFolderListBinding>(FragmentFolderListBinding::inflate), MediaFragment {
    companion object {
        private const val FOLDER_FRAGMENT_TAG = "SUB_FOLDER_TAG"
    }

    private val getFolderUseCase: GetFoldersUserCase by lazy {
        GetFoldersUserCase(
            LocalMediaRepositoryImpl(
                requireContext().applicationContext
            )
        )
    }
    private val adapter by lazy {
        object :
            AbstractAdapter<FolderModel, ItemFolderListBinding>(ItemFolderListBinding::inflate) {
            override fun bind(binding: ItemFolderListBinding, item: FolderModel) {
                binding.thumb.load(item.thumb)
                binding.title.text = item.title
                binding.fileCount.text = item.fileCount.toString()
                binding.totalSize.text = item.totalSize.toReadableSize()
                binding.root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    private fun onItemClick(item: FolderModel) {
        binding.fragmentContainer.replaceFragment(
            MediaListFragment().also {
                it.arguments = Bundle().apply {
                    putLong("init_delay", 200)
                    putString("folder_name", item.title)
                }
            }, FOLDER_FRAGMENT_TAG,
            enterAnimation = R.anim.slide_up,
            exitAnimation = R.anim.slide_down,
            popEnterAnimation = R.anim.slide_up,
            popExitAnimation = R.anim.slide_down
        )
    }

    override fun initView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayout.VERTICAL
            )
        )
        CoroutineScope(Dispatchers.IO).launch {
            val folders = getFolderUseCase(null)
            CoroutineScope(Dispatchers.Main).launch {
                if (folders is ResultData.Success) {
                    adapter.submitList(folders.data)
                }
            }
        }
    }

    override fun toggleSelectAll() {
        childFragmentManager.findFragmentByTag(FOLDER_FRAGMENT_TAG)?.let {
            if (it is MediaFragment) {
                it.toggleSelectAll()
            }
        }
    }

    override fun handleBackPress(): Boolean {
        if (childFragmentManager.findFragmentByTag(FOLDER_FRAGMENT_TAG)!=null) {
            childFragmentManager.popBackStack()
            return true
        }
        return false
    }

}