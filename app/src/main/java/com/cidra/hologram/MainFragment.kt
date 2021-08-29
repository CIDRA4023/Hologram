package com.cidra.hologram

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        val packageName = "com.cidra.Hologram"
        val uri: Uri = Uri.parse("market://details?id=$packageName")

        when (item.itemId) {
            // アプリの共有
            R.id.share -> {
                val shareData = getString(R.string.share_app_data)

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareData)
                }
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(
                        Intent.createChooser(
                            intent,
                            null
                        )
                    )
                } else {
                    Toast.makeText(requireContext(), R.string.not_found_share, Toast.LENGTH_SHORT).show()
                }
            }
            // このアプリについて
            R.id.about_app -> {
                findNavController().navigate(R.id.action_mainFragment_to_aboutThisAppFragment)

            }
            // レビューを投稿する
            R.id.review -> {

                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
                }
            }

            R.id.preference -> {
                findNavController().navigate(R.id.action_mainFragment_to_preferenceFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}

