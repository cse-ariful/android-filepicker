package org.arif.converter.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<B : ViewBinding>(private val inflater: Inflater<B>) : Fragment() {
    /**
     * holder for the viewBinding instance for current fragment
     */
    lateinit var binding: B

    /**
     * Method called after onViewCreated lifecycle method
     * proper place for initializing view with your data
     */
    abstract fun initView()
    override fun onCreateView(lInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.invoke(lInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val delayTime = arguments?.getLong("init_delay") ?: 0
        Log.d("TAG", "onViewCreated: delay $delayTime ${arguments.toString()}")
        if (delayTime > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(delayTime)
                CoroutineScope(Dispatchers.Main).launch {
                    Handler(Looper.getMainLooper()).post { initView() }
                }
            }
        } else {
            Handler(Looper.getMainLooper()).post { initView() }
        }
    }


    fun View.replaceFragment(
        fragment: Fragment,
        tag: String,
        allowStateLoss: Boolean = true,
        addtoBackStack: Boolean = true,
        @AnimRes enterAnimation: Int = 0,
        @AnimRes exitAnimation: Int = 0,
        @AnimRes popEnterAnimation: Int = 0,
        @AnimRes popExitAnimation: Int = 0
    ) {
        val ft = childFragmentManager
            .beginTransaction()
            .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
            .replace(this.id, fragment, tag)
        if (addtoBackStack)
            ft.addToBackStack(tag)
        if (!childFragmentManager.isStateSaved) {
            ft.commit()
        } else if (allowStateLoss) {
            ft.commitAllowingStateLoss()
        }
    }
}