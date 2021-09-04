package com.cidra.hologram.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cidra.hologram.TagList
import com.cidra.hologram.data.LiveItem
import com.cidra.hologram.data.NoneItem
import com.cidra.hologram.databinding.ItemArchivesBinding
import com.google.android.material.chip.Chip


class NoneListAdapter(val clickListener: NoneListListener) :
    ListAdapter<NoneItem, NoneListAdapter.VideoListViewHolder>(DiffCallback) {


    inner class VideoListViewHolder(private var binding: ItemArchivesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nones: NoneItem, clickListener: NoneListListener) {
            binding.item = nones
            binding.clickListener = clickListener
            binding.executePendingBindings()
            // get bind text
            val chipGroup = binding.chipGroupArchive
            chipGroup.children.forEach {
                val a = it as Chip
                Log.i("chipCount", "${a.text}")
                onClickChip(it)
            }
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

    /**
     * chipをクリックしたときの処理
     * intentを使ってチャンネルページに飛ぶようにする
     */
    fun onClickChip(chip: Chip){
        val tagText = chip.text
        val idList = TagList.nameToId
        val categoryList = TagList.categoryName

        if (categoryList.contains(tagText)) {
            return
        } else {
            chip.setOnClickListener {
                Log.i("chipText", "$tagText")
                val baseUrl = "https://www.youtube.com/channel/"
                val intent = Intent(Intent.ACTION_VIEW)
                Log.i("chipText", "$idList[$tagText]")
                intent.data = Uri.parse(baseUrl + idList["$tagText"])
                val context = it.context
                context.startActivity(intent)
            }
        }
    }

}

class NoneListListener(val clickListener: (id: String) -> Unit) {
    fun onClick(item: NoneItem) = clickListener(item.videoId)
}