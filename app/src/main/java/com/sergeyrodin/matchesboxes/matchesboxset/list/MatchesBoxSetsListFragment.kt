package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxSetsListBinding

/**
 * A simple [Fragment] subclass.
 */
class MatchesBoxSetsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchesBoxSetsListBinding.inflate(inflater)
        val viewModel by viewModels<MatchesBoxSetsListViewModel> {
            MatchesBoxSetsListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        val args by navArgs<MatchesBoxSetsListFragmentArgs>()
        viewModel.start(args.bagId)

        val adapter = MatchesBoxSetAdapter(MatchesBoxSetListener{
            viewModel.selectItem(it)
        })
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = adapter

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(
                        ADD_NEW_ITEM_ID, args.bagId)
            )
        })

        return binding.root
    }

}
