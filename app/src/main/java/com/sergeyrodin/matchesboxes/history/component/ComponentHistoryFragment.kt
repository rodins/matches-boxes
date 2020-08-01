package com.sergeyrodin.matchesboxes.history.component

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentComponentHistoryBinding

class ComponentHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentComponentHistoryBinding.inflate(inflater)
        val viewModel by viewModels<ComponentHistoryViewModel> {
            ComponentHistoryViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        val args by navArgs<ComponentHistoryFragmentArgs>()
        viewModel.start(args.componentId)
        binding.displayComponentHistoryList.adapter = DisplayComponentHistoryAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

}