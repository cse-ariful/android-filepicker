package com.nightcode.mediapicker.presentation.fragments.mediaList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nightcode.mediapicker.databinding.FragmentMediaListBinding
import com.nightcode.mediapicker.domain.AppConstants
import com.nightcode.mediapicker.domain.entities.VideoModel
import com.nightcode.mediapicker.domain.interfaces.VideoPickerInterface
import com.nightcode.mediapicker.domain.viewModels.mediaList.MediaListViewModel
import com.nightcode.mediapicker.domain.viewModels.mediaList.MyViewModelFactory
import com.nightcode.mediapicker.presentation.ViewExtension.hide
import com.nightcode.mediapicker.presentation.ViewExtension.show
import com.nightcode.mediapicker.domain.adapters.MediaListAdapter
import com.nightcode.mediapicker.domain.constants.LayoutMode
import com.nightcode.mediapicker.domain.interfaces.MediaFragment
import org.arif.converter.fragments.BaseFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MediaListFragment :
    BaseFragment<FragmentMediaListBinding>(FragmentMediaListBinding::inflate), MediaFragment {
    private val viewModel by viewModels<MediaListViewModel> { MyViewModelFactory(requireContext()) }
    var callback: VideoPickerInterface? = null
    private lateinit var selectedFiles: LiveData<List<VideoModel>>
    var layoutMode = MutableLiveData(LayoutMode.GRID)

    override fun onCreateView(
        lInflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(lInflater, container, savedInstanceState)
        if (arguments?.getString("folder_name") != null) {
            binding.fab.show()
        } else {
            binding.fab.hide()
        }
        return binding.root
    }

    /*private val adapter = object : AbstractAdapter<VideoModel, ItemVideoGridBinding>(ItemVideoGridBinding::inflate) {
        override fun bind(binding: ItemVideoGridBinding, item: VideoModel) {
            selectedFiles?.observe(viewLifecycleOwner, Observer {
                item.selected = it.firstOrNull { file -> file.uri == item.uri } != null
                binding.selectionView.visibility = item.selected.asVisibility()
                binding.root.scaleX = if (item.selected) 0.8f else 1f
                binding.root.scaleY = if (item.selected) 0.8f else 1f
            })
            Glide.with(binding.thumb)
                .load(item.uri)
                .placeholder(R.drawable.video_placeholder)
                .into(binding.thumb)
            binding.duration.text = item.duration.toReadableTime()
            binding.size.text = item.size.toReadableSize()
            binding.thumb.setOnClickListener {
                callback?.updateSelection(item)
            }
        }
    }*/
    private lateinit var adapter: MediaListAdapter

    override fun initView() {
        viewModel.setFolderName(arguments?.getString("folder_name"))
        binding.fab.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        selectedFiles = callback?.getSelectedFiles() ?: MutableLiveData()
        adapter = MediaListAdapter(
            selectedFile = selectedFiles,
            viewLifecycleOwner,
            callback = object : MediaListAdapter.Callback {
                override fun onItemClick(videoModel: VideoModel) {
                    callback?.updateSelection(videoModel = videoModel)
                }
            })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.visibility = View.GONE
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            binding.swiperRefresh.isRefreshing = it
            if (it) {
                binding.recyclerView.hide()//.animatedHide()
                binding.progressbar.show()//animatedShow()
            } else {
                binding.recyclerView.show()//animatedShow()
                binding.progressbar.hide()//animatedHide()
            }
        })
        viewModel.files.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        binding.swiperRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        adapter.layoutMode = LayoutMode.LIST
        layoutMode.observe(viewLifecycleOwner, Observer {
            adapter.layoutMode = it
            setUpLayoutManager(it)
        })
        setUpLayoutManager(layoutMode = layoutMode.value!!)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(mode: LayoutMode) {
        layoutMode.postValue(mode)
    }

    private fun setUpLayoutManager(layoutMode: LayoutMode) {
        if (layoutMode == LayoutMode.LIST) {
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
        } else {
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        if (this@MediaListFragment.layoutMode.value == LayoutMode.LIST) return 3
                        if (AppConstants.isNativeAdPosition(position))
                            return 3
                        return 1
                    }
                }
            }
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is VideoPickerInterface) {
            callback = parentFragment as VideoPickerInterface
        }
        if (activity is VideoPickerInterface) {
            callback = activity as VideoPickerInterface
        }
    }

    override fun toggleSelectAll() {
        callback?.updateSelection(adapter.getAllItems())
    }

    override fun handleBackPress(): Boolean {
        return false
    }

    fun search(query: String?) {

        viewModel.search(query)
    }

    fun setLayoutMode(mode: LayoutMode) {
        layoutMode.postValue(mode)
    }
}