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
    private lateinit var binding: FragmentComponentHistoryBinding
    private val args by navArgs<ComponentHistoryFragmentArgs>()
    private val viewModel by viewModels<ComponentHistoryViewModel> {
        getViewModelFactory()
    }

    private fun getViewModelFactory(): ComponentHistoryViewModelFactory {
        return ComponentHistoryViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createBinding(inflater)
        startViewModel()
        setupBinding()
        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater) {
        binding = FragmentComponentHistoryBinding.inflate(inflater)
    }

    private fun startViewModel() {
        viewModel.start(args.componentId)
    }

    private fun setupBinding() {
        binding.displayComponentHistoryList.adapter = DisplayComponentHistoryAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
}