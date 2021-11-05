package com.sergeyrodin.matchesboxes.needed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NeededComponentsFragment : Fragment() {
    private val viewModel by viewModels<NeededComponentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_needed_components, container, false)
        view.findViewById<ComposeView>(R.id.needed_components_compose_view).setContent {
            AppCompatTheme {
                NeededComponentsScreen(viewModel = viewModel)
            }
        }

        viewModel.selectedComponentEvent.observe(viewLifecycleOwner, EventObserver { componentId ->
            navigateToRadioComponentDetailsFragment(componentId)
        })

        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddComponentNoException()
        })

        return view
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