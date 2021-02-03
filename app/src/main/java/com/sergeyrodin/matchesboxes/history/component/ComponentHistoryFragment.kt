package com.sergeyrodin.matchesboxes.history.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.databinding.FragmentComponentHistoryBinding
import com.sergeyrodin.matchesboxes.history.HistoryActionModeController
import com.sergeyrodin.matchesboxes.history.all.HistoryLongClickListener

class ComponentHistoryFragment : Fragment() {
    private lateinit var actionModeController: HistoryActionModeController
    private lateinit var binding: FragmentComponentHistoryBinding
    private lateinit var adapter: HistoryAdapter
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
        createActionModeController()
        startViewModel()
        createAdapter()
        setupBinding()
        observeHighlightedItemIdEvent()
        observeActionModeEvent()

        viewModel.deltas.observe(viewLifecycleOwner, {
            adapter.deltas = it
        })

        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater) {
        binding = FragmentComponentHistoryBinding.inflate(inflater)
    }

    private fun createActionModeController() {
        actionModeController = HistoryActionModeController(requireActivity(), viewModel)
    }

    private fun startViewModel() {
        viewModel.start(args.componentId)
    }

    private fun createAdapter() {
         adapter = HistoryAdapter(
            createLongClickListener(),
            createClickListener()
        )
        binding.displayComponentHistoryList.adapter = adapter
    }

    private fun createLongClickListener(): HistoryLongClickListener {
        return HistoryLongClickListener { id ->
            viewModel.presentationLongClick(id)
        }
    }

    private fun createClickListener(): HistoryClickListener {
        return HistoryClickListener {
            viewModel.presentationClick()
        }
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun observeHighlightedItemIdEvent() {
        viewModel.highlightedItemIdEvent.observe(viewLifecycleOwner, Observer { id ->
            adapter.highlightedItemId = id
        })
    }

    private fun observeActionModeEvent() {
        viewModel.actionModeEvent.observe(viewLifecycleOwner, Observer { activateActionMode ->
            if (activateActionMode) {
                actionModeController.startActionMode()
            } else {
                actionModeController.finishActionMode()
            }
        })
    }
}