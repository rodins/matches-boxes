package com.sergeyrodin.matchesboxes.history.all

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentHistoryAllBinding

class HistoryAllFragment : Fragment() {
    private var actionMode: ActionMode? = null
    private val actionModeCallback = createActionModeCallback()

    private fun createActionModeCallback(): ActionMode.Callback {
        return object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                inflateMenu(mode, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                if (item.itemId == R.id.action_delete) {
                    viewModel.deleteHighlightedPresentation()
                    return true
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                actionMode = null
            }
        }
    }

    private fun inflateMenu(mode: ActionMode, menu: Menu) {
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
    }

    private lateinit var binding: FragmentHistoryAllBinding
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
        setupBinding()
        setupObservers()
        setHasOptionsMenu(false)
        return binding.root
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentHistoryAllBinding.inflate(inflater, container, false)
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.displayHistoryList.adapter = createDisplayHistoryAdapter()
    }

    private fun createDisplayHistoryAdapter(): HistoryPresentationAdapter {
        return HistoryPresentationAdapter(
            createHistoryPresentationClickListener(),
            createHistoryPresentationLongClickListener()
        )
    }

    private fun createHistoryPresentationClickListener(): HistoryPresentationClickListener {
        return HistoryPresentationClickListener { position ->
            viewModel.presentationClick(position)
        }
    }

    private fun createHistoryPresentationLongClickListener(): HistoryPresentationClickListener {
        return HistoryPresentationClickListener { position ->
            viewModel.presentationLongClick(position)
        }
    }

    private fun setupObservers() {
        observeSelectedEvent()
        observeActionDeleteVisibilityEvent()
        observeItemChangedEvent()
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

    private fun observeActionDeleteVisibilityEvent() {
        viewModel.actionDeleteVisibilityEvent.observe(viewLifecycleOwner, EventObserver { actionModeEnabled ->
            if(actionModeEnabled) {
                startActionMode()
            }else {
                finishActionMode()
            }
        })
    }

    private fun startActionMode() {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
        }
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }

    private fun observeItemChangedEvent() {
        viewModel.itemChangedEvent.observe(viewLifecycleOwner, EventObserver { position ->
            binding.displayHistoryList.adapter?.notifyItemChanged(position)
        })
    }
}