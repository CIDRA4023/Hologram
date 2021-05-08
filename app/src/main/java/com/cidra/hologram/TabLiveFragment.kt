package com.cidra.hologram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        val adapter = LiveListAdapter(LiveListListener {
            val baseUrl = "https://www.youtube.com/watch?v="
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(baseUrl + it)
            startActivity(intent)
        })

        binding.videoList.adapter = adapter

        return binding.root
    }

}

