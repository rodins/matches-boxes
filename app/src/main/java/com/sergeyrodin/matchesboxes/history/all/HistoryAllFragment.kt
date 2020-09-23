package com.sergeyrodin.matchesboxes.history.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
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

    private fun createDisplayHistoryAdapter(): DisplayHistoryAdapter {
        return DisplayHistoryAdapter(
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


}