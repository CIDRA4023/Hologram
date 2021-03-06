package com.cidra.hologram

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
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

        val transitionPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val transitionStatus = transitionPreference.getString("transitionApp", "youtubeApp")

        val adapter = UpcomingListAdapter(viewModel, UpcomingListListener {
            val baseUrl = "https://www.youtube.com/watch?v="
            when (transitionStatus) {
                "youtubeApp" -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(baseUrl + it)
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("error ", "$e")
                    }
                }
                else -> {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://"))
                        val defaultResInfo =
                            context?.packageManager?.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + it))
                        if (defaultResInfo != null) {
                            intent.setPackage(defaultResInfo.activityInfo.packageName)
                            startActivity(intent)
                        }

                    } catch (e: ActivityNotFoundException) {
                        Log.e("error ", "$e")
                    }
                }
            }
        })

        // viewmodelFactory?????????????????????
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val settingStatus = sharedPreference.getString("setGroup", "hololive")

        viewModel.todayItem.observe(viewLifecycleOwner, Observer { todayItemList ->
            viewModel.tomorrowItem.observe(viewLifecycleOwner, Observer { tomorrowItemList ->
                var item = listOf<ListItem>()
                /**
                 * ???????????????????????????????????????????????????????????????
                 */
                when (settingStatus) {
                    "hololive" -> {
                        item = when {
                            todayItemList.isEmpty() -> listOf(ListItem.TomorrowHeader) + tomorrowItemList.map {
                                ListItem.TomorrowItem(it)
                            }
                                .filter { it.tomorrow.tagGroup == "holoJp" || it.tomorrow.tagGroup == "holoId" || it.tomorrow.tagGroup == "holoEn" }

                            tomorrowItemList.isEmpty() -> listOf(ListItem.TodayHeader) + todayItemList.map {
                                ListItem.TodayItem(it)
                            }
                                .filter { it.today.tagGroup == "holoJp" || it.today.tagGroup == "holoId" || it.today.tagGroup == "holoEn" }

                            todayItemList.isEmpty() && tomorrowItemList.isEmpty() -> listOf()

                            else -> listOf(ListItem.TodayHeader) + todayItemList.map {
                                ListItem.TodayItem(it)
                            }
                                .filter { it.today.tagGroup == "holoJp" || it.today.tagGroup == "holoId" || it.today.tagGroup == "holoEn" } + listOf(
                                ListItem.TomorrowHeader
                            ) + tomorrowItemList.map { ListItem.TomorrowItem(it) }
                                .filter { it.tomorrow.tagGroup == "holoJp" || it.tomorrow.tagGroup == "holoId" || it.tomorrow.tagGroup == "holoEn" }
                        }
                    }
                    "holoStars" -> {
                        item = when {
                            todayItemList.isEmpty() -> listOf(ListItem.TomorrowHeader) + tomorrowItemList.map {
                                ListItem.TomorrowItem(
                                    it
                                )
                            }.filter { it.tomorrow.tagGroup == "holoStars" }

                            tomorrowItemList.isEmpty() -> listOf(ListItem.TodayHeader) + todayItemList.map {
                                ListItem.TodayItem(
                                    it
                                )
                            }.filter { it.today.tagGroup == "holoStars" }

                            todayItemList.isEmpty() && tomorrowItemList.isEmpty() -> listOf()

                            else -> listOf(ListItem.TodayHeader) + todayItemList.map {
                                ListItem.TodayItem(
                                    it
                                )
                            }.filter { it.today.tagGroup == "holoStars" } + listOf(
                                ListItem.TomorrowHeader
                            ) + tomorrowItemList.map { ListItem.TomorrowItem(it) }
                                .filter { it.tomorrow.tagGroup == "holoStars" }
                        }

                    }
                    else -> {
                        item = when {
                            todayItemList.isEmpty() -> listOf(ListItem.TomorrowHeader) + tomorrowItemList.map {
                                ListItem.TomorrowItem(it)
                            }

                            tomorrowItemList.isEmpty() -> listOf(ListItem.TodayHeader) + todayItemList.map {
                                ListItem.TodayItem(it)
                            }

                            todayItemList.isEmpty() && tomorrowItemList.isEmpty() -> listOf()

                            else -> listOf(ListItem.TodayHeader) + todayItemList.map {
                                ListItem.TodayItem(it)
                            } + listOf(
                                ListItem.TomorrowHeader
                            ) + tomorrowItemList.map { ListItem.TomorrowItem(it) }

                        }

                    }

                }

                adapter.submitList(item)
            })
        })

        binding.upcomingList.adapter = adapter

        return binding.root
    }
}