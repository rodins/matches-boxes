package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.databinding.FragmentBagManipulatorBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class BagManipulatorFragment : Fragment() {
    private lateinit var viewModel: BagManipulatorViewModel
    private val args by navArgs<BagManipulatorFragmentArgs>()
    private var isActionDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = createBinding(inflater)
        viewModel = createViewModel()
        setupBinding(binding)
        setupIsActionDeleteVisible()
        startViewModel()
        observeAddedEvent()
        observeEditedEvent()
        observeDeletedEvent()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater): FragmentBagManipulatorBinding {
        return FragmentBagManipulatorBinding.inflate(inflater)
    }

    private fun createViewModel(): BagManipulatorViewModel {
        val viewModel by viewModels<BagManipulatorViewModel> {
            BagManipulatorViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        return viewModel
    }

    private fun setupBinding(binding: FragmentBagManipulatorBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
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
