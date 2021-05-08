package com.cidra.hologram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.databinding.ItemArchivesBinding


class NoneListAdapter(val clickListener: NoneListListener) :
    ListAdapter<NoneItem, NoneListAdapter.VideoListViewHolder>(DiffCallback) {


    inner class VideoListViewHolder(private var binding: ItemArchivesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nones: NoneItem, clickListener: NoneListListener) {
            binding.item = nones
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NoneItem>() {
        override fun areItemsTheSame(oldItem: NoneItem, newItem: NoneItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: NoneItem, newItem: NoneItem): Boolean {
            return oldItem.title == newItem.title
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder {
        val view = ItemArchivesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoListViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video, clickListener)
    }
}

class NoneListListener(val clickListener: (id: String) -> Unit) {
    fun onClick(item: NoneItem) = clickListener(item.videoID)
}