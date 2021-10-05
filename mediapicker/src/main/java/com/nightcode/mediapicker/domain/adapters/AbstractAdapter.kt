package com.nightcode.mediapicker.domain.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.BuildConfig
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

@SuppressLint("NotifyDataSetChanged")
abstract class AbstractAdapter<T, B : ViewBinding>(private val inflate: Inflate<B>) :
    RecyclerView.Adapter<MyViewHolder<B>>() {

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                onDataSetChanged(items)
            }
        })
    }

    var inflater: LayoutInflater? = null
    private val items = mutableListOf<T>()

    // abstract fun createView(inflater: LayoutInflater, parent: ViewGroup): B
    abstract fun bind(binding: B, item: T)
    open fun onDataSetChanged(newItems: List<T>) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<B> {
        inflater = inflater ?: LayoutInflater.from(parent.context)
        return MyViewHolder(inflate.invoke(inflater!!, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder<B>, position: Int) {
        bind(holder.binding, getItem(position))
    }

    protected open fun createBindingInstance(inflater: LayoutInflater, container: ViewGroup?): B {
        val vbType = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val vbClass = vbType as Class<B>
        val method =
            vbClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
        // Call VB.inflate(inflater, container, false) Java static method

        return method.invoke(null, inflater, container, false) as B
    }

    private fun getItem(position: Int): T {
        return items[position]
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<T>?) {
        if (BuildConfig.DEBUG && Thread.currentThread().name != "main") {
            throw IllegalStateException("Set items from main thread. Setting items from other thread don't update items")
        }
        items.clear()
        if (newItems != null)
            items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        val count = itemCount
        items.clear()
        notifyDataSetChanged()
    }

    fun updateItem(item: T, position: Int) {
        if (items.size <= position || position < 0) return
        items[position] = item
        notifyItemChanged(position)
    }

}

class MyViewHolder<B : ViewBinding>(internal val binding: B) : RecyclerView.ViewHolder(binding.root)