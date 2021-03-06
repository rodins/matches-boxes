package com.sergeyrodin.matchesboxes.history.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.databinding.FragmentComponentHistoryBinding
import com.sergeyrodin.matchesboxes.history.HistoryActionModeController
import com.sergeyrodin.matchesboxes.history.all.HistoryLongClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComponentHistoryFragment : Fragment() {
    private lateinit var actionModeController: HistoryActionModeController
    private lateinit var binding: FragmentComponentHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val args by navArgs<ComponentHistoryFragmentArgs>()

    private val viewModel by viewModels<ComponentHistoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createBinding(inflater)
        createActionModeController()
        startViewModel()
        createAdapter()
        setupBinding()
        observeHighlightedItemIdEvent()
        observeActionModeEvent()

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
        viewModel.highlightedPositionEvent.observe(viewLifecycleOwner, { position ->
            adapter.notifyItemChanged(position)
        })
    }

    private fun observeActionModeEvent() {
        viewModel.actionModeEvent.observe(viewLifecycleOwner, { activateActionMode ->
            if (activateActionMode) {
                actionModeController.startActionMode()
            } else {
                actionModeController.finishActionMode()
            }
        })
    }
}