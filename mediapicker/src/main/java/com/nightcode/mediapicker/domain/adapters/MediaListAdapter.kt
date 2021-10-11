package com.nightcode.mediapicker.domain.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nightcode.mediapicker.R
import com.nightcode.mediapicker.databinding.*
import com.nightcode.mediapicker.domain.AppConstants
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.constants.LayoutMode
import com.nightcode.mediapicker.presentation.ViewExtension.toReadableSize
import com.nightcode.mediapicker.presentation.ViewExtension.toReadableTime

@SuppressLint("NotifyDataSetChanged")
class MediaListAdapter(
    val selectedFile: LiveData<List<VideoModel>>,
    private val lifecycleOwner: LifecycleOwner,
    val callback: Callback
) :
    RecyclerView.Adapter<ViewHolder>() {
    interface Callback {
        fun onItemClick(videoModel: VideoModel)
    }

    companion object {
        const val TYPE_VIDEO_LIST = 0
        const val TYPE_VIDEO_GRID = 1
        const val TYPE_AD = 2
    }

    private var inflater: LayoutInflater? = null
    var layoutMode: LayoutMode = LayoutMode.GRID
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private val items = mutableListOf<VideoModel>()


    fun submitList(newData: List<VideoModel>?) {
        this.items.clear()
        newData?.let { this.items.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        inflater = inflater ?: LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_VIDEO_GRID -> {
                return VideoItemGridViewHolder(
                    ItemVideoGridBinding.inflate(
                        inflater!!,
                        parent,
                        false
                    )
                )
            }
            TYPE_VIDEO_LIST -> {
                return VideoItemListViewHolder(
                    ItemVideoListBinding.inflate(
                        inflater!!,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return AdViewHolder(ItemAdViewBinding.inflate(inflater!!, parent, false))
            }
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is VideoItemGridViewHolder) {
            val item = getItem(position, TYPE_VIDEO_GRID)!!
            holder.bind(selectedFile, lifecycleOwner, callback, item)
        } else if (holder is VideoItemListViewHolder) {
            val item = getItem(position, TYPE_VIDEO_LIST)!!
            holder.bind(selectedFile, lifecycleOwner, callback, item)
        }
    }

    private fun getItem(position: Int, viewType: Int): VideoModel? {
        if (viewType == TYPE_AD) return null
        return items[AppConstants.getRealPositionIgnoringAd(position)]
    }

    override fun getItemViewType(position: Int): Int {
        if (AppConstants.isNativeAdPosition(position)) {
            return TYPE_AD
        }
        return if (layoutMode == LayoutMode.GRID)
            TYPE_VIDEO_GRID
        else
            TYPE_VIDEO_LIST
    }

    override fun getItemCount(): Int = AppConstants.getTotalItemCountWithNativeAd(items.size)
    fun getAllItems(): List<VideoModel> {
        return items
    }
}

class VideoItemGridViewHolder(private val binding: ItemVideoGridBinding) :
    ViewHolder(binding.root) {
    fun bind(
        selectedFiles: LiveData<List<VideoModel>>,
        lifecycleOwner: LifecycleOwner,
        callback: MediaListAdapter.Callback,
        item: VideoModel
    ) {
        selectedFiles.observe(lifecycleOwner, Observer {
            item.selected = it.firstOrNull { file -> file.uri == item.uri } != null
            binding.checkIcon.setImageResource(if (item.selected) R.drawable.ic_baseline_check_circle_24 else R.drawable.ic_outline_radio_button_unchecked_24)
            //binding.root.scaleX = if (item.selected) 0.8f else 1f
           // binding.root.scaleY = if (item.selected) 0.8f else 1f
            binding.thumb.alpha = if(item.selected)0.4f else 1f
        })
        Glide.with(binding.thumb)
            .load(item.uri)
            .placeholder(R.drawable.video_placeholder)
            .into(binding.thumb)
        binding.duration.text = item.duration.toReadableTime()
        binding.size.text = item.size.toReadableSize()
        binding.thumb.setOnClickListener {
            callback.onItemClick(item)
        }

    }
}

class VideoItemListViewHolder(private val binding: ItemVideoListBinding) :
    ViewHolder(binding.root) {
    fun bind(
        selectedFiles: LiveData<List<VideoModel>>,
        lifecycleOwner: LifecycleOwner,
        callback: MediaListAdapter.Callback,
        item: VideoModel
    ) {
        selectedFiles.observe(lifecycleOwner, Observer {
            item.selected = it.firstOrNull { file -> file.uri == item.uri } != null
            binding.checkIcon.setImageResource(if (item.selected) R.drawable.ic_baseline_check_circle_24 else R.drawable.ic_outline_radio_button_unchecked_24)
            //binding.root.scaleX = if (item.selected) 0.8f else 1f
           // binding.root.scaleY = if (item.selected) 0.8f else 1f
            binding.thumb.alpha = if(item.selected)0.4f else 1f
        })
        Glide.with(binding.thumb)
            .load(item.uri)
            .placeholder(R.drawable.video_placeholder)
            .into(binding.thumb)
        binding.title.text = item.title
        binding.duration.text = item.duration.toReadableTime()
        binding.size.text = item.size.toReadableSize()
        binding.container.setOnClickListener {
            callback.onItemClick(item)
        }

    }
}

abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
}