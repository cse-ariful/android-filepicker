package com.nightcode.mediapicker.presentation.fragments.browseMedia

import android.content.Intent
import com.nightcode.mediapicker.databinding.FragmentBrowseMediaBinding
import org.arif.converter.fragments.BaseFragment
import androidx.core.app.ActivityCompat.startActivityForResult
import com.nightcode.mediapicker.domain.AppConstants


class BrowseMediaFragment :
    BaseFragment<FragmentBrowseMediaBinding>(FragmentBrowseMediaBinding::inflate) {


    override fun initView() {
        binding.browseMediaBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            requireActivity().startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                AppConstants.MEDIA_PICK_REQUEST_CODE
            )

        }
    }
}