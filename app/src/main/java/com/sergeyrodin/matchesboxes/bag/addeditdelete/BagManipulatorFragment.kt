package com.sergeyrodin.matchesboxes.bag.addeditdelete

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
import com.sergeyrodin.matchesboxes.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagManipulatorFragment : Fragment() {
    private val viewModel: BagManipulatorViewModel by viewModels()
    private val args by navArgs<BagManipulatorFragmentArgs>()
    private var isActionDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_bag_manipulator, container, false)
        view.findViewById<ComposeView>(R.id.bag_compose_view).setContent {
            AppCompatTheme {
                BagNameScreen(viewModel)
            }
        }
        setupIsActionDeleteVisible()
        startViewModel()
        observeAddedEvent()
        observeEditedEvent()
        observeDeletedEvent()
        setHasOptionsMenu(true)
        return view
    }

    private fun setupIsActionDeleteVisible() {
        isActionDeleteVisible = args.bagId != ADD_NEW_ITEM_ID
    }

    private fun startViewModel() {
        viewModel.start(args.bagId)
    }

    private fun observeDeletedEvent() {
        viewModel.eventDeleted.observe(viewLifecycleOwner, EventObserver {
            displayBagDeletedToast()
            navigateToBagsListFragment()
        })
    }

    private fun navigateToBagsListFragment() {
        findNavController().navigate(
            BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
        )
    }

    private fun displayBagDeletedToast() {
        Toast.makeText(requireContext(), R.string.bag_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun observeEditedEvent() {
        viewModel.eventEdited.observe(viewLifecycleOwner, EventObserver { title ->
            hideKeyboard(activity)
            displayBagUpdatedToast()
            navigateToMatchesBoxSetListFragment(title)
        })
    }

    private fun navigateToMatchesBoxSetListFragment(title: String) {
        findNavController().navigate(
            BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToMatchesBoxSetsListFragment(
                args.bagId,
                title
            )
        )
    }

    private fun displayBagUpdatedToast() {
        Toast.makeText(requireContext(), R.string.bag_updated, Toast.LENGTH_SHORT).show()
    }

    private fun observeAddedEvent() {
        viewModel.eventAdded.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard(activity)
            displayBagAddedToast()
            navigateToBagsListFragment()
        })
    }

    private fun displayBagAddedToast() {
        Toast.makeText(requireContext(), R.string.bag_added, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        setActionDeleteVisibility(menu)
    }

    private fun setActionDeleteVisibility(menu: Menu) {
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = isActionDeleteVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteBag()
        }
        return super.onOptionsItemSelected(item)
    }
}
