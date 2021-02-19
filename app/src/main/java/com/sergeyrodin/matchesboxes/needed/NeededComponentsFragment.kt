package com.sergeyrodin.matchesboxes.needed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentListener
import com.sergeyrodin.matchesboxes.databinding.FragmentNeededComponentsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class NeededComponentsFragment : Fragment() {
    private val viewModel by viewModels<NeededComponentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = createBinding(inflater, container)
        setupBinding(binding)
        observeAddComponentEvent()
        return binding.root
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNeededComponentsBinding.inflate(inflater, container, false)

    private fun setupBinding(binding: FragmentNeededComponentsBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = createAdapter()
    }

    private fun createAdapter(): RadioComponentAdapter {
        return RadioComponentAdapter(RadioComponentListener { componentId ->
            navigateToRadioComponentDetailsFragment(componentId)
        })
    }

    private fun navigateToRadioComponentDetailsFragment(componentId: Int) {
        findNavController().navigate(
            NeededComponentsFragmentDirections
                .actionNeededComponentsFragmentToRadioComponentDetailsFragment(
                    componentId,
                    "",
                    RadioComponentManipulatorReturns.TO_NEEDED_LIST
                )
        )
    }

    private fun observeAddComponentEvent() {
        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddComponentNoException()
        })
    }

    private fun navigateToAddComponentNoException() {
        try {
            navigateToAddComponentFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToAddComponentFragment() {
        findNavController().navigate(
            NeededComponentsFragmentDirections
                .actionNeededComponentsFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID,
                    NO_ID_SET,
                    getString(R.string.add_component),
                    "",
                    RadioComponentManipulatorReturns.TO_NEEDED_LIST
                )
        )
    }

    private fun showNavigationErrorToast() {
        Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
    }
}