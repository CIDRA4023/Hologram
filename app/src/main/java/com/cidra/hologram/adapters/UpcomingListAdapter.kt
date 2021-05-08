package com.cidra.hologram.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cidra.hologram.R
import com.cidra.hologram.data.UpcomingItem
import com.cidra.hologram.databinding.ItemScheduleBinding
import com.cidra.hologram.viewmodels.ScheduleViewModel


private const val TODAY_HEADER = 0
private const val TODAY_ITEM = 1
private const val TOMORROW_HEADER = 2
private const val TOMORROW_ITEM = 3

sealed class ListItem {
    object TodayHeader : ListItem() {
        override val id = "0"
    }

    object TomorrowHeader : ListItem() {
        override val id = "1"
    }

    class TodayItem(val today: UpcomingItem) : ListItem() {
        override val id = today.videoID
    }

    class TomorrowItem(val tomorrow: UpcomingItem) : ListItem() {
        override val id = tomorrow.videoID
    }

    abstract val id: String

}

class UpcomingListAdapter(
    val viewModel: ScheduleViewModel,
    val clickListener: UpcomingListListener
) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(DiffCallback) {

    /**
     * アイテム用ViewHolder
     */
    class TodayListViewHolder(private var binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.TodayItem, clickListener: UpcomingListListener) {
            binding.item = item.today
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TodayListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemScheduleBinding.inflate(layoutInflater, parent, false)
                return TodayListViewHolder(binding)
            }
        }
    }

    /**
     * アイテム用ViewHolder
     */
    class TomorrowListViewHolder(private var binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.TomorrowItem, clickListener: UpcomingListListener) {
            binding.item = item.tomorrow
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TomorrowListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemScheduleBinding.inflate(layoutInflater, parent, false)
                return TomorrowListViewHolder(binding)
            }
        }
    }

    /**
     * header用ViewHolder
     */
    class TodayHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TodayHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_header_today, parent, false)
                return TodayHeaderViewHolder(view)
            }
        }
    }

    /**
     * header用ViewHolder
     */
    class TomorrowHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TomorrowHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_header_tomorrow, parent, false)
                return TomorrowHeaderViewHolder(view)
            }
        }
    }

    /**
     * positionによってヘッダー、アイテムを入れる位置を決める
     */
    override fun getItemViewType(position: Int): Int {

        return when (getItem(position) as ListItem) {
            is ListItem.TodayHeader -> TODAY_HEADER
            is ListItem.TodayItem -> TODAY_ITEM
            is ListItem.TomorrowHeader -> TOMORROW_HEADER
            is ListItem.TomorrowItem -> TOMORROW_ITEM
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodayListViewHolder -> {
                val item = getItem(position) as ListItem.TodayItem
                holder.bind(item, clickListener)
            }
            is TomorrowListViewHolder -> {
                val item = getItem(position) as ListItem.TomorrowItem
                holder.bind(item, clickListener)
            }
        }

    }

    /**
     * viewTypeに応じてViewHolder生成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TODAY_HEADER -> TodayHeaderViewHolder.from(parent)
            TODAY_ITEM -> TodayListViewHolder.from(parent)
            TOMORROW_HEADER -> TomorrowHeaderViewHolder.from(parent)
            TOMORROW_ITEM -> TomorrowListViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

    }
}

class UpcomingListListener(val clickListener: (id: String) -> Unit) {
    fun onClick(item: UpcomingItem) = clickListener(item.videoID)
}



