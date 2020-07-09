package com.sergeyrodin.matchesboxes.matchesbox.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityListener
import com.sergeyrodin.matchesboxes.common.list.CommonViewModel
import com.sergeyrodin.matchesboxes.common.list.CommonViewModelFactory

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

        viewModel.startBox(args.setId)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = DisplayQuantityAdapter(DisplayQuantityListener{
            viewModel.selectBox(it)
        })

        viewModel.addBoxEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MatchesBoxListFragmentDirections
                    .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxFragment(args.setId, ADD_NEW_ITEM_ID, getString(R.string.add_box))
            )
        })

        viewModel.selectBoxEvent.observe(viewLifecycleOwner, EventObserver{ selectedBox ->
            findNavController().navigate(
                MatchesBoxListFragmentDirections
                    .actionMatchesBoxListFragmentToRadioComponentsListFragment(selectedBox.id, selectedBox.name)
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
                    .actionMatchesBoxListFragmentToAddEditDeleteMatchesBoxSetFragment(
                        ADD_NEW_ITEM_ID, args.setId, getString(R.string.update_set))
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
