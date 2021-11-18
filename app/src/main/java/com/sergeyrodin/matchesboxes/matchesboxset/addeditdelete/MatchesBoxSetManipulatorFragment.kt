package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

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
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesBoxSetManipulatorFragment : Fragment() {

    private val viewModel: MatchesBoxSetManipulatorViewModel by viewModels()
    private val args by navArgs<MatchesBoxSetManipulatorFragmentArgs>()
    private var isDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_matches_box_set_manipulator, container, false)
        view.findViewById<ComposeView>(R.id.set_compose_view).setContent {
            AppCompatTheme {
                SetNameScreen(viewModel = viewModel)
            }
        }
        setupIsDeleteVisible()
        startViewModel()
        observeAddEvent()
        observeUpdateEvent()
        observeDeleteEvent()
        setHasOptionsMenu(true)
        return view
    }

    private fun setupIsDeleteVisible() {
        isDeleteVisible = args.setId != ADD_NEW_ITEM_ID
    }

    private fun startViewModel() {
        viewModel.start(args.bagId, args.setId)
    }

    private fun observeAddEvent() {
        viewModel.addedEvent.observe(viewLifecycleOwner, EventObserver { title ->
            hideKeyboard(activity)
            makeToastForAddEvent()
            navigateToMatchesBoxSetListFragment(title)
        })
    }

    private fun makeToastForAddEvent() {
        Toast.makeText(context, R.string.matches_box_set_added, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMatchesBoxSetListFragment(title: String) {
        findNavController().navigate(
            MatchesBoxSetManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(
                    args.bagId,
                    title
                )
        )
    }

    private fun observeUpdateEvent() {
        viewModel.updatedEvent.observe(viewLifecycleOwner, EventObserver { set ->
            hideKeyboard(activity)
            makeToastForUpdateEvent()
            navigateToMatchesBoxListFragment(set)
        })
    }

    private fun makeToastForUpdateEvent() {
        Toast.makeText(context, R.string.matches_box_set_updated, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMatchesBoxListFragment(set: MatchesBoxSet) {
        findNavController().navigate(
            MatchesBoxSetManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxListFragment(
                    args.setId,
                    set.name
                )
        )
    }

    private fun observeDeleteEvent() {
        viewModel.deletedEvent.observe(viewLifecycleOwner, EventObserver { bag ->
            makeToastForDeleteEvent()
            navigateToMatchesBoxSetListFragment(bag)
        })
    }

    private fun makeToastForDeleteEvent() {
        Toast.makeText(context, R.string.matches_box_set_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMatchesBoxSetListFragment(bag: Bag) {
        findNavController().navigate(
            MatchesBoxSetManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(
                    bag.id,
                    bag.name
                )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        setupActionDeleteVisibility(menu)
    }

    private fun setupActionDeleteVisibility(menu: Menu) {
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = isDeleteVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBoxSet()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
