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
        observeSelectedEvent()
        observeActionDeleteVisibilityEvent()
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
        return HistoryPresentationClickListener { presentation ->
            viewModel.presentationClick(presentation)
        }
    }

    private fun createHistoryPresentationLongClickListener(): HistoryPresentationLongClickListener {
        return HistoryPresentationLongClickListener { id ->
            viewModel.presentationLongClick(id)
        }
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
        viewModel.actionDeleteVisibilityEvent.observe(viewLifecycleOwner, EventObserver { visible ->
            setHasOptionsMenu(visible)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteHighlightedPresentation()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}