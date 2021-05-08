package com.cidra.hologram.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cidra.hologram.TabArchivesFragment
import com.cidra.hologram.TabLiveFragment
import com.cidra.hologram.TabScheduleFragment

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> TabLiveFragment()
            1 -> TabScheduleFragment()
            else -> TabArchivesFragment()
        }
}