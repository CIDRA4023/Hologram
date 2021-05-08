package com.cidra.hologram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cidra.hologram.adapters.NoneListAdapter
import com.cidra.hologram.adapters.NoneListListener
import com.cidra.hologram.databinding.FragmentTabArchivesBinding
import com.cidra.hologram.viewmodels.ArchivesViewModel

class TabArchivesFragment : Fragment() {

    lateinit var viewModel: ArchivesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(ArchivesViewModel::class.java)

        val binding = FragmentTabArchivesBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = NoneListAdapter(NoneListListener {
            val baseUrl = "https://www.youtube.com/watch?v="
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(baseUrl + it)
            startActivity(intent)
        })

        binding.videoList.adapter = adapter

        return binding.root
    }
}