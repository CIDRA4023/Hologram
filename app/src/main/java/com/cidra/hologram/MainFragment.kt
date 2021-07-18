package com.cidra.hologram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.app_name)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.item_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // プライバシーポリシー
            R.id.policy -> {
                val policyUrl = "https://github.com/CIDRA4023/Hologram/blob/master/PrivacyPolicy.md"
                val intentPolicy = Intent(Intent.ACTION_VIEW)
                intentPolicy.data = Uri.parse(policyUrl)
                startActivity(intentPolicy)
            }
            // 利用規約
            R.id.terms -> {
                val termsUrl = "https://github.com/CIDRA4023/Hologram/blob/master/Terms.md"
                val intentTerms = Intent(Intent.ACTION_VIEW)
                intentTerms.data = Uri.parse(termsUrl)
                startActivity(intentTerms)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}

