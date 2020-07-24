package com.sergeyrodin.matchesboxes.searchbuy.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentListener
import com.sergeyrodin.matchesboxes.databinding.FragmentSearchBuyBinding


class SearchBuyListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBuyBinding.inflate(inflater)
        val viewModel by viewModels<SearchBuyListViewModel> {
            SearchBuyListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        val args by navArgs<SearchBuyListFragmentArgs>()

        viewModel.start(args.query, args.isSearch)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = RadioComponentAdapter(RadioComponentListener {
            findNavController().navigate(
                SearchBuyListFragmentDirections.actionSearchBuyFragmentToRadioComponentDetailsFragment(it, args.query, !args.isSearch)
            )
        })

        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                SearchBuyListFragmentDirections.actionSearchBuyFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID, NO_ID_SET, getString(R.string.add_component), args.query, !args.isSearch)
            )
        })

        return binding.root
    }
}