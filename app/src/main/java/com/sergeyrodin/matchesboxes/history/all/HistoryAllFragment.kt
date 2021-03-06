package com.sergeyrodin.matchesboxes.history.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.databinding.FragmentHistoryAllBinding
import com.sergeyrodin.matchesboxes.history.HistoryActionModeController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryAllFragment : Fragment() {
    private lateinit var actionModeController: HistoryActionModeController
    private lateinit var binding: FragmentHistoryAllBinding
    private lateinit var adapter: HistoryModelAdapter

    private val viewModel by viewModels<HistoryAllViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createBinding(inflater, container)
        createActionModeController()
        setupBinding()
        setupObservers()

        return binding.root
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentHistoryAllBinding.inflate(inflater, container, false)
    }

    private fun createActionModeController() {
        actionModeController = HistoryActionModeController(requireActivity(), viewModel)
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        adapter = createDisplayHistoryAdapter()
        binding.displayHistoryList.adapter = adapter
    }

    private fun createDisplayHistoryAdapter(): HistoryModelAdapter {
        return HistoryModelAdapter(
            createHistoryPresentationClickListener(),
            createHistoryPresentationLongClickListener()
        )
    }

    private fun createHistoryPresentationClickListener(): HistoryModelClickListener {
        return HistoryModelClickListener { componentId, name ->
            viewModel.presentationClick(componentId, name)
        }
    }

    private fun createHistoryPresentationLongClickListener(): HistoryLongClickListener {
        return HistoryLongClickListener { id ->
            viewModel.presentationLongClick(id)
        }
    }

    private fun setupObservers() {
        observeSelectedEvent()
        observeActionModeEvent()
        observeHighlightedPositionEvent()
    }

    private fun observeSelectedEvent() {
        viewModel.selectedEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToComponentHistoryFragment(it.componentId, it.name)
        })
    }

    private fun navigateToComponentHistoryFragment(id: Int, name: String) {
        findNavController().navigate(
            HistoryAllFragmentDirections.actionHistoryAllFragmentToComponentHistoryFragment(
                id,
                name
            )
        )
    }

    private fun observeActionModeEvent() {
        viewModel.actionModeEvent.observe(viewLifecycleOwner, { actionModeEnabled ->
            if(actionModeEnabled) {
                actionModeController.startActionMode()
            }else {
                actionModeController.finishActionMode()
            }
        })
    }

    private fun observeHighlightedPositionEvent() {
        viewModel.highlightedPositionEvent.observe(viewLifecycleOwner, {
            adapter.notifyItemChanged(it)
        })
    }
}