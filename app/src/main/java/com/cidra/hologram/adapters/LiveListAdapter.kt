package com.cidra.hologram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.databinding.ItemLiveBinding


class LiveListAdapter(val clickListener: LiveListListener) :
    ListAdapter<LiveItem, LiveListAdapter.VideoListViewHolder>(DiffCallback) {


    inner class VideoListViewHolder(private var binding: ItemLiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(live: LiveItem, clickListener: LiveListListener) {
            binding.item = live
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<LiveItem>() {
        override fun areItemsTheSame(oldItem: LiveItem, newItem: LiveItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LiveItem, newItem: LiveItem): Boolean {
            return oldItem.title == newItem.title
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder {
        val view = ItemLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoListViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video, clickListener)

    }

}

class LiveListListener(val clickListener: (id: String) -> Unit) {
    fun onClick(item: LiveItem) = clickListener(item.videoID)
}