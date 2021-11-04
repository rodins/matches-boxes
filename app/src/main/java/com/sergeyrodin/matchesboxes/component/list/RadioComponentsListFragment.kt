package com.sergeyrodin.matchesboxes.component.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.DO_NOT_NEED_THIS_VARIABLE
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RadioComponentsListFragment : Fragment() {
    private val args by navArgs<RadioComponentsListFragmentArgs>()
    private val viewModel by viewModels<RadioComponentsListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_radio_components_list, container, false)
        view.findViewById<ComposeView>(R.id.components_compose_view).setContent {
            AppCompatTheme {
                ComponentsScreen(viewModel)
            }
        }

        startViewModel()

        observeAddComponentEvent()
        observeSelectComponentEvent()

        setHasOptionsMenu(true)
        return view
    }

    private fun observeSelectComponentEvent() {
        viewModel.selectedComponentEvent.observe(viewLifecycleOwner, EventObserver { id ->
            findNavController().navigate(
                RadioComponentsListFragmentDirections
                    .actionRadioComponentsListFragmentToRadioComponentDetailsFragment(
                        id,
                        "",
                        RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                    )
            )
        })
    }

    private fun startViewModel() {
        viewModel.startComponent(args.boxId)
    }

    private fun observeAddComponentEvent() {
        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddComponentNoException()
        })
    }

    private fun navigateToAddComponentNoException() {
        try {
            navigateToAddFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToAddFragment() {
        findNavController().navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID,
                    args.boxId,
                    getString(R.string.add_component),
                    "",
                    RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                )
        )
    }

    private fun showNavigationErrorToast() {
        Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_edit) {
            navigateToEditMatchesBoxFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToEditMatchesBoxFragment() {
        findNavController().navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteMatchesBoxFragment(
                    DO_NOT_NEED_THIS_VARIABLE, args.boxId, getString(R.string.update_box)
                )
        )
    }
}