package com.cidra.hologram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cidra.hologram.adapters.TabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val viewPager2 = view.findViewById<ViewPager2>(R.id.pager_main)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_main)

        val pagerAdapter = TabAdapter(this)
        viewPager2.adapter = pagerAdapter

        val mediator = TabLayoutMediator(
            tabLayout,
            viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.tab_live)
                    1 -> tab.text = getString(R.string.tab_schedule)
                    else -> tab.text = getString(R.string.tab_archives)
                }
            }
        ).attach()
        // Inflate the layout for this fragment
        return view
    }

}