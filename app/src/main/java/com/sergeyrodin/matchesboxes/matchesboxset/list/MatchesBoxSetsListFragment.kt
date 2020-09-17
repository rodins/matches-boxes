package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityListener
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxSetsListBinding

class MatchesBoxSetsListFragment : Fragment() {

    private val args by navArgs<MatchesBoxSetsListFragmentArgs>()
    private val viewModel by viewModels<MatchesBoxSetsListViewModel> {
        getViewModelFactory()
    }

    private fun getViewModelFactory(): MatchesBoxSetsListViewModelFactory {
        return MatchesBoxSetsListViewModelFactory(
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

    private fun createBinding(inflater: LayoutInflater): FragmentMatchesBoxSetsListBinding {
        return FragmentMatchesBoxSetsListBinding.inflate(inflater)
    }

    private fun startViewModel() {
        viewModel.startSet(args.bagId)
    }

    private fun setupBinding(binding: FragmentMatchesBoxSetsListBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = createAdapter()
    }

    private fun createAdapter(): DisplayQuantityAdapter {
        return DisplayQuantityAdapter(DisplayQuantityListener {
            viewModel.selectSet(it)
        }, R.drawable.ic_set)
    }

    private fun observeAddEvent() {
        viewModel.addSetEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToMatchesBoxSetManipulatorFragment()
        })
    }

    private fun navigateToMatchesBoxSetManipulatorFragment() {
        findNavController().navigate(
            MatchesBoxSetsListFragmentDirections
                .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(
                    args.bagId, ADD_NEW_ITEM_ID, getString(R.string.add_set)
                )
        )
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
