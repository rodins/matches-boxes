package com.sergeyrodin.matchesboxes.matchesbox.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityListener
import com.sergeyrodin.matchesboxes.data.MatchesBox

import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxListBinding

class MatchesBoxListFragment : Fragment() {

    private val args by navArgs<MatchesBoxListFragmentArgs>()
    private val viewModel by viewModels<MatchesBoxListViewModel> {
        getViewModelFactory()
    }

    private fun getViewModelFactory(): MatchesBoxListViewModelFactory {
        return MatchesBoxListViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = createBinding(inflater)
        startViewModel()
        setupBinding(binding)
        observeAddEvent()
        observeSelectEvent()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater) =
        FragmentMatchesBoxListBinding.inflate(inflater)

    private fun startViewModel() {
        viewModel.startBox(args.setId)
    }

    private fun setupBinding(binding: FragmentMatchesBoxListBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = createAdapter()
    }

    private fun createAdapter(): DisplayQuantityAdapter {
        return DisplayQuantityAdapter(DisplayQuantityListener {
            viewModel.selectBox(it)
        })
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
            navigateToMatchesBoxManipulatorFragment()
        })
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
