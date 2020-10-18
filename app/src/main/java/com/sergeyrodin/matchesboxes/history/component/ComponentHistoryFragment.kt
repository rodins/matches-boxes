package com.sergeyrodin.matchesboxes.history.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.databinding.FragmentComponentHistoryBinding
import com.sergeyrodin.matchesboxes.history.HistoryActionModeController
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationClickListener

class ComponentHistoryFragment : Fragment() {
    private lateinit var actionModeController: HistoryActionModeController
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
        createActionModeController()
        startViewModel()
        createAdapter()
        setupBinding()
        observeItemChangedEvent()
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
        binding.displayComponentHistoryList.adapter = DisplayComponentHistoryAdapter(
            createLongClickListener(),
            createClickListener()
        )
    }

    private fun createLongClickListener(): HistoryPresentationClickListener {
        return HistoryPresentationClickListener { position ->
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
        viewModel.itemChangedEvent.observe(viewLifecycleOwner, Observer { position ->
            binding.displayComponentHistoryList.adapter?.notifyItemChanged(position)
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