package com.nightcode.mediapicker.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
typealias Inflater<T> = (LayoutInflater) -> T
abstract class BaseActivity<B : ViewBinding>(private val inflater: Inflater<B>) : AppCompatActivity() {
    lateinit var binding: B
    abstract fun initView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflater.invoke(layoutInflater)
        setContentView(binding.root)
        initView()
    }
}