package com.sergeyrodin.matchesboxes.history.component

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentComponentHistoryBinding
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationLongClickListener

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
        createAdapter()
        setupBinding()
        observeItemChangedEvent()
        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater) {
        binding = FragmentComponentHistoryBinding.inflate(inflater)
    }

    private fun startViewModel() {
        viewModel.start(args.componentId)
    }

    private fun createAdapter() {
        binding.displayComponentHistoryList.adapter = DisplayComponentHistoryAdapter(
            createLongClickListener(),
            createClickListener()
        )
    }

    private fun createLongClickListener(): HistoryPresentationLongClickListener {
        return HistoryPresentationLongClickListener { position ->
            viewModel.presentationLongClick(position)
        }
    }

    private fun createClickListener(): ComponentHistoryPresentationClickListener {
        return ComponentHistoryPresentationClickListener {
            viewModel.presentationClick()
        }
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun observeItemChangedEvent() {
        viewModel.itemChangedEvent.observe(viewLifecycleOwner, EventObserver { position ->
            binding.displayComponentHistoryList.adapter?.notifyItemChanged(position)
        })
    }
}