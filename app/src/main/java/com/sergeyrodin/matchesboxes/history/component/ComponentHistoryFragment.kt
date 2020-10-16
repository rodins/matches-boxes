package com.sergeyrodin.matchesboxes.history.component

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentComponentHistoryBinding
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationClickListener

class ComponentHistoryFragment : Fragment() {
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
                viewModel.actionModeClosed()
                actionMode = null
            }
        }
    }

    private fun inflateMenu(mode: ActionMode, menu: Menu) {
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.delete_menu, menu)
    }

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
        viewModel.actionModeEvent.observe(viewLifecycleOwner, Observer{ activateActionMode ->
            if(activateActionMode) {
                if(actionMode == null) {
                    actionMode = activity?.startActionMode(actionModeCallback)
                }
            }else {
                actionMode?.finish()
            }
        })
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
        viewModel.itemChangedEvent.observe(viewLifecycleOwner, EventObserver { position ->
            binding.displayComponentHistoryList.adapter?.notifyItemChanged(position)
        })
    }
}