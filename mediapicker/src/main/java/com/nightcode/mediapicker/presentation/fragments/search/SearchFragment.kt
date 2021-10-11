package com.nightcode.mediapicker.presentation.fragments.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.nightcode.mediapicker.R
import com.nightcode.mediapicker.databinding.FragmentSearchBinding
import com.nightcode.mediapicker.domain.constants.LayoutMode
import com.nightcode.mediapicker.presentation.fragments.folderList.FragmentFolderList
import com.nightcode.mediapicker.presentation.fragments.mediaList.MediaListFragment
import org.arif.converter.fragments.BaseFragment
import androidx.core.content.ContextCompat.getSystemService
import java.lang.Exception


class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private var mediaListFragment: MediaListFragment? = null
    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed({
            mediaListFragment = MediaListFragment()
            mediaListFragment?.setLayoutMode(LayoutMode.LIST)
            binding.fragmentContainer.replaceFragment(
                mediaListFragment!!,
                "TAG",
                addtoBackStack = false
            )
            openKeyboard()
        }, 150)
        binding.backBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.searchField.addTextChangedListener {
            mediaListFragment?.search(it?.toString())
        }

    }

    private fun openKeyboard() {
        try {
            binding.searchField.requestFocus()
            val inputMethodManager: InputMethodManager? =
                binding.searchField.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
            inputMethodManager?.toggleSoftInputFromWindow(
                binding.searchField.windowToken,
                InputMethodManager.SHOW_FORCED, 0
            )
        } catch (ex: Exception) {
        }
    }

    override fun onPause() {
        binding.searchField.clearFocus()
        super.onPause()

    }
}