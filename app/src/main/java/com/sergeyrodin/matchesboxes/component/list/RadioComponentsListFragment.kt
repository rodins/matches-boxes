package com.sergeyrodin.matchesboxes.component.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentsListBinding

class RadioComponentsListFragment : Fragment() {
    private val args by navArgs<RadioComponentsListFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRadioComponentsListBinding.inflate(inflater)
        val viewModel by viewModels<RadioComponentsListViewModel>{
            RadioComponentsListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        viewModel.start(args.boxId)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = RadioComponentAdapter(RadioComponentListener {
            findNavController().navigate(
                RadioComponentsListFragmentDirections
                    .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(args.boxId, it)
            )
        })

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                RadioComponentsListFragmentDirections
                    .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(args.boxId, ADD_NEW_ITEM_ID)
            )
        })

        viewModel.boxTitle.observe(viewLifecycleOwner, Observer{
            (activity as MainActivity).supportActionBar?.title = it
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
                RadioComponentsListFragmentDirections.actionRadioComponentsListFragmentToAddEditDeleteMatchesBoxFragment(
                    DO_NOT_NEED_THIS_VARIABLE, args.boxId)
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}