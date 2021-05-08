package com.cidra.hologram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cidra.hologram.adapters.ListItem
import com.cidra.hologram.adapters.UpcomingListAdapter
import com.cidra.hologram.adapters.UpcomingListListener
import com.cidra.hologram.databinding.FragmentTabScheduleBinding
import com.cidra.hologram.viewmodels.ScheduleViewModel

class TabScheduleFragment : Fragment() {

    lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        val binding = FragmentTabScheduleBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = UpcomingListAdapter(viewModel, UpcomingListListener {
            val baseUrl = "https://www.youtube.com/watch?v="
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(baseUrl + it)
            startActivity(intent)
        })

        viewModel.todayItem.observe(viewLifecycleOwner, Observer { todayItemList ->
            viewModel.tomorrowItem.observe(viewLifecycleOwner, Observer { tomorrowItemList ->
                /**
                 * 今日、明日のアイテムの有無によって条件分岐
                 */
                val item = when {
                    todayItemList.isEmpty() -> listOf(ListItem.TomorrowHeader) + tomorrowItemList.map {
                        ListItem.TomorrowItem(
                            it
                        )
                    }
                    tomorrowItemList.isEmpty() -> listOf(ListItem.TodayHeader) + todayItemList.map {
                        ListItem.TodayItem(
                            it
                        )
                    }
                    todayItemList.isEmpty() && tomorrowItemList.isEmpty() -> listOf()
                    else -> listOf(ListItem.TodayHeader) + todayItemList.map { ListItem.TodayItem(it) } + listOf(
                        ListItem.TomorrowHeader
                    ) + tomorrowItemList.map { ListItem.TomorrowItem(it) }
                }
                adapter.submitList(item)
            })
        })

        binding.upcomingList.adapter = adapter

        return binding.root
    }
}