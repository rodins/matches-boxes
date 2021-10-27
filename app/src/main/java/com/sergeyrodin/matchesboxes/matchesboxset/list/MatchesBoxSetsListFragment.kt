package com.sergeyrodin.matchesboxes.matchesboxset.list

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
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesBoxSetsListFragment : Fragment() {

    private val args by navArgs<MatchesBoxSetsListFragmentArgs>()
    private val viewModel by viewModels<MatchesBoxSetsListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matches_box_sets_list, container, false)
        view.findViewById<ComposeView>(R.id.sets_compose_view).setContent {
            AppCompatTheme {
                SetsScreen(viewModel)
            }
        }

        startViewModel()
        observeAddEvent()
        observeSelectEvent()
        setHasOptionsMenu(true)
        return view
    }

    private fun startViewModel() {
        viewModel.startSet(args.bagId)
    }

    private fun observeAddEvent() {
        viewModel.addSetEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToSetManipulatorFragmentNoException()
        })
    }

    private fun navigateToSetManipulatorFragmentNoException() {
        try {
            navigateToMatchesBoxSetManipulatorFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToMatchesBoxSetManipulatorFragment() {
        findNavController().navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(
                    args.bagId, ADD_NEW_ITEM_ID, getString(R.string.add_set)
                )
        )
    }

    private fun showNavigationErrorToast() {
        Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
    }

    private fun observeSelectEvent() {
        viewModel.selectSetEvent.observe(viewLifecycleOwner, EventObserver { set ->
            navigateToMatchesBoxListFragment(set)
        })
    }

    private fun navigateToMatchesBoxListFragment(set: MatchesBoxSet) {
        findNavController().navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToMatchesBoxListFragment(set.id, set.name)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_edit) {
            navigateToBagManipulatorFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToBagManipulatorFragment() {
        findNavController().navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteBagFragment(
                    args.bagId,
                    getString(R.string.update_bag)
                )
        )
    }

}
