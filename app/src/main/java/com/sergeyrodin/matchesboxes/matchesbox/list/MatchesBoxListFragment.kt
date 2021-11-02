package com.sergeyrodin.matchesboxes.matchesbox.list

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
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.MatchesBox
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesBoxListFragment : Fragment() {

    private val args by navArgs<MatchesBoxListFragmentArgs>()
    private val viewModel by viewModels<MatchesBoxListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_matches_box_list, container, false)
        view.findViewById<ComposeView>(R.id.sets_compose_view).setContent {
            AppCompatTheme {
                BoxesScreen(viewModel)
            }
        }
        startViewModel()
        observeAddEvent()
        observeSelectEvent()
        setHasOptionsMenu(true)
        return view
    }

    private fun startViewModel() {
        viewModel.startBox(args.setId)
    }

    private fun observeSelectEvent() {
        viewModel.selectBoxEvent.observe(viewLifecycleOwner, EventObserver { selectedBox ->
            navigateToRadioComponentListFragment(selectedBox)
        })
    }

    private fun navigateToRadioComponentListFragment(selectedBox: MatchesBox) {
        findNavController().navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToRadioComponentsListFragment(
                    selectedBox.id,
                    selectedBox.name
                )
        )
    }

    private fun observeAddEvent() {
        viewModel.addBoxEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToSetManipulatorFragmentNoException()
        })
    }

    private fun navigateToSetManipulatorFragmentNoException() {
        try {
            navigateToMatchesBoxManipulatorFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToMatchesBoxManipulatorFragment() {
        findNavController().navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxFragment(
                    args.setId,
                    ADD_NEW_ITEM_ID,
                    getString(R.string.add_box)
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
            navigateToSetOfBoxesManipulatorFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToSetOfBoxesManipulatorFragment() {
        findNavController().navigate(
            MatchesBoxListFragmentDirections
                .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxSetFragment(
                    ADD_NEW_ITEM_ID, args.setId, getString(R.string.update_set)
                )
        )
    }
}
