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
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.cidra.hologram.adapters.LiveListAdapter
import com.cidra.hologram.adapters.LiveListListener
import com.cidra.hologram.databinding.FragmentTabLiveBinding
import com.cidra.hologram.viewmodels.LiveViewModel

class TabLiveFragment : Fragment() {

    lateinit var viewModel: LiveViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LiveViewModel::class.java)

        val binding = FragmentTabLiveBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val settingStatus = sharedPreference.getString("transitionApp", "youtubeApp")

        val adapter = LiveListAdapter(LiveListListener {
            val baseUrl = "https://www.youtube.com/watch?v="
            when (settingStatus) {
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

        binding.videoList.adapter = adapter

        return binding.root
    }

}

