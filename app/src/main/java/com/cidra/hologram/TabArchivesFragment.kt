package com.cidra.hologram

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cidra.hologram.adapters.NoneListAdapter
import com.cidra.hologram.adapters.NoneListListener
import com.cidra.hologram.databinding.FragmentTabArchivesBinding
import com.cidra.hologram.utilities.dateAgo
import com.cidra.hologram.viewmodels.ArchivesViewModel
import java.text.SimpleDateFormat
import java.util.*

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
            try {
                val baseUrl = "https://www.youtube.com/watch?v="
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(baseUrl + it)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("error ", "$e")
            }
        })

        val sdfD = SimpleDateFormat("dd", Locale.getDefault())
        binding.chipAgo2days.text = sdfD.format(dateAgo(-2))
        binding.chipAgo3days.text = sdfD.format(dateAgo(-3))
        binding.chipAgo4days.text = sdfD.format(dateAgo(-4))
        binding.chipAgo5days.text = sdfD.format(dateAgo(-5))
        binding.chipAgo6days.text = sdfD.format(dateAgo(-6))
        binding.chipAgo7days.text = sdfD.format(dateAgo(-7))

        binding.chipGroup.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                R.id.chip_today -> {
                    viewModel.filterSelectedChip(dateAgo(0), 0)
                }
                R.id.chip_yesterday -> {
                    viewModel.filterSelectedChip(dateAgo(-1), -1)
                }
                R.id.chip_ago_2days -> {
                    viewModel.filterSelectedChip(dateAgo(-2), -2)
                }
                R.id.chip_ago_3days -> {
                    viewModel.filterSelectedChip(dateAgo(-3), -3)
                }
                R.id.chip_ago_4days -> {
                    viewModel.filterSelectedChip(dateAgo(-4), -4)
                }
                R.id.chip_ago_5days -> {
                    viewModel.filterSelectedChip(dateAgo(-5), -5)
                }
                R.id.chip_ago_6days -> {
                    viewModel.filterSelectedChip(dateAgo(-6), -6)
                }
                R.id.chip_ago_7days -> {
                    viewModel.filterSelectedChip(dateAgo(-7), -7)
                }
            }
        }

        binding.videoList.adapter = adapter

        return binding.root
    }
}