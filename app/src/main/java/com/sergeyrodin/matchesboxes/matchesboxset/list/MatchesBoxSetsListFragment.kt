package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityListener
import com.sergeyrodin.matchesboxes.common.list.CommonViewModel
import com.sergeyrodin.matchesboxes.common.list.CommonViewModelFactory
import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxSetsListBinding

/**
 * A simple [Fragment] subclass.
 */
class MatchesBoxSetsListFragment : Fragment() {

    private val args by navArgs<MatchesBoxSetsListFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchesBoxSetsListBinding.inflate(inflater)
        val viewModel by viewModels<CommonViewModel> {
            CommonViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        if(activity is MainActivity) {
            (activity as MainActivity).supportActionBar?.title = args.bag.name
        }

        viewModel.startSet(args.bag.id)

        val adapter = DisplayQuantityAdapter(DisplayQuantityListener{
            viewModel.selectSet(it)
        })
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = adapter

        viewModel.addSetEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(
                        args.bag, null)
            )
        })

        viewModel.selectSetEvent.observe(viewLifecycleOwner, EventObserver{ set ->
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToMatchesBoxListFragment(set)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_edit) {
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToAddEditDeleteBagFragment(args.bag)
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
