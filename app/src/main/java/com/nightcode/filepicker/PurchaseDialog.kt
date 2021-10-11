package com.nightcode.filepicker

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nightcode.filepicker.databinding.PurchaseDialogBinding

class PurchaseDialog : DialogFragment() {
    companion object {
        fun newInstance(productInfo: ProductModel): PurchaseDialog {
            val args = Bundle()
            args.putSerializable("product", productInfo)
            val fragment = PurchaseDialog()
            fragment.arguments = args

            return fragment
        }
    }

    lateinit var binding: PurchaseDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PurchaseDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun getTheme(): Int {
        return R.style.MyAlertDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productInfo = arguments?.getSerializable("product") as? ProductModel?
        if(productInfo!=null){

        }
    }
}