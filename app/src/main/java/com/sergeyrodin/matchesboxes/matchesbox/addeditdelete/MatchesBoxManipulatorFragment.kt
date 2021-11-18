package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

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
import com.sergeyrodin.matchesboxes.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesBoxManipulatorFragment : Fragment() {
    private val viewModel by viewModels<MatchesBoxManipulatorViewModel>()
    private val args by navArgs<MatchesBoxManipulatorFragmentArgs>()

    private var hideDelete = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_matches_box_manipulator, container, false)
        view.findViewById<ComposeView>(R.id.box_compose_view).setContent {
            AppCompatTheme {
                BoxNameScreen(viewModel = viewModel)
            }
        }
        setupHideDeleteButton()
        startViewModel()
        observeAddEvent()
        observeUpdateEvent()
        observeDeleteEvent()
        setHasOptionsMenu(true)
        return view
    }

    private fun setupHideDeleteButton() {
        hideDelete = args.boxId != ADD_NEW_ITEM_ID
    }

    private fun startViewModel() {
        viewModel.start(args.setId, args.boxId)
    }

    private fun observeAddEvent() {
        viewModel.addEvent.observe(viewLifecycleOwner, EventObserver { title ->
            hideKeyboard(activity)
            makeToastForAddEvent()
            navigateToMatchesBoxListFragment(title)
        })
    }

    private fun makeToastForAddEvent() {
        Toast.makeText(context, R.string.box_added, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMatchesBoxListFragment(title: String) {
        findNavController().navigate(
            MatchesBoxManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(
                    args.setId,
                    title
                )
        )
    }

    private fun observeUpdateEvent() {
        viewModel.updateEvent.observe(viewLifecycleOwner, EventObserver { title ->
            hideKeyboard(activity)
            makeToastForUpdatedEvent()
            navigateToRadioComponentListFragment(title)
        })
    }

    private fun navigateToRadioComponentListFragment(title: String) {
        findNavController().navigate(
            MatchesBoxManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToRadioComponentsListFragment(
                    args.boxId,
                    title
                )
        )
    }

    private fun makeToastForUpdatedEvent() {
        Toast.makeText(context, R.string.box_updated, Toast.LENGTH_SHORT).show()
    }

    private fun observeDeleteEvent() {
        viewModel.deleteEvent.observe(viewLifecycleOwner, EventObserver { set ->
            makeToastForDeleteEvent()
            navigateToMatchesBoxListFragment(set)
        })
    }

    private fun makeToastForDeleteEvent() {
        Toast.makeText(context, R.string.box_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMatchesBoxListFragment(set: MatchesBoxSet) {
        findNavController().navigate(
            MatchesBoxManipulatorFragmentDirections
                .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(set.id, set.name)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        setupActionDeleteVisibility(menu)
    }

    private fun setupActionDeleteVisibility(menu: Menu) {
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = hideDelete
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBox()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}