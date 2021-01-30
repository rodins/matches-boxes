package com.sergeyrodin.matchesboxes.history.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.databinding.FragmentHistoryAllBinding
import com.sergeyrodin.matchesboxes.history.HistoryActionModeController

class HistoryAllFragment : Fragment() {
    private lateinit var actionModeController: HistoryActionModeController
    private lateinit var binding: FragmentHistoryAllBinding
    private lateinit var adapter: HistoryPresentationAdapter

    private val viewModel by viewModels<HistoryAllViewModel> {
        getViewModelFactory()
    }

    private fun getViewModelFactory(): HistoryAllViewModelFactory {
        return HistoryAllViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    private fun createDisplayHistoryAdapter(): HistoryPresentationAdapter {
        return HistoryPresentationAdapter(
            createHistoryPresentationClickListener(),
            createHistoryPresentationLongClickListener()
        )
    }

    private fun createHistoryPresentationClickListener(): HistoryPresentationClickListener {
        return HistoryPresentationClickListener { id ->
            viewModel.presentationClick(id)
        }
    }

    private fun createHistoryPresentationLongClickListener(): HistoryPresentationClickListener {
        return HistoryPresentationClickListener { id ->
            viewModel.presentationLongClick(id)
        }
    }

    private fun setupObservers() {
        observeSelectedEvent()
        observeActionModeEvent()
        observeHighlightedIdEvent()
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
        viewModel.actionModeEvent.observe(viewLifecycleOwner, Observer { actionModeEnabled ->
            if(actionModeEnabled) {
                actionModeController.startActionMode()
            }else {
                actionModeController.finishActionMode()
            }
        })
    }

    private fun observeHighlightedIdEvent() {
        viewModel.highlightedIdEvent.observe(viewLifecycleOwner, Observer { id ->
            adapter.highlightedItemId = id
        })
    }
}