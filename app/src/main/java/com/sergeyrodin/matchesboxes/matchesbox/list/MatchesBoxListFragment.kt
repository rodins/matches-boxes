package com.sergeyrodin.matchesboxes.matchesbox.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityListener

import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxListBinding

class MatchesBoxListFragment : Fragment() {


    private val args by navArgs<MatchesBoxListFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchesBoxListBinding.inflate(inflater)
        val viewModel by viewModels<MatchesBoxListViewModel> {
            MatchesBoxListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        viewModel.setTitle.observe(viewLifecycleOwner, Observer {
            if(activity is MainActivity) {
                (activity as MainActivity).supportActionBar?.title = it
            }
        })

        viewModel.start(args.set)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = DisplayQuantityAdapter(DisplayQuantityListener{
            viewModel.selectMatchesBox(it)
        })

        viewModel.addMatchesBoxEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MatchesBoxListFragmentDirections
                    .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxFragment(ADD_NEW_ITEM_ID, args.bag, args.set)
            )
        })

        viewModel.selectMatchesBoxEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MatchesBoxListFragmentDirections
                    .actionMatchesBoxListFragmentToRadioComponentsListFragment(it, args.bag, args.set)
            )
        })

        binding.items.adapter = adapter

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
                MatchesBoxListFragmentDirections
                    .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxSetFragment(args.bag, args.set)
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
