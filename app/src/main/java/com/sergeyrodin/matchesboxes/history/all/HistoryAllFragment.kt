package com.sergeyrodin.matchesboxes.history.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.databinding.FragmentHistoryAllBinding

class HistoryAllFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryAllBinding.inflate(inflater, container, false)
        val viewModel by viewModels<HistoryAllViewModel> {
            HistoryAddViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.displayHistoryList.adapter = DisplayHistoryAdapter(DisplayHistoryListener { id ->
            Toast.makeText(context, id.toString(), Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }

}