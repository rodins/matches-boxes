package com.sergeyrodin.matchesboxes.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentListener
import com.sergeyrodin.matchesboxes.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        val viewModel by viewModels<SearchViewModel> {
            SearchViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        val args by navArgs<SearchFragmentArgs>()

        viewModel.start(args.query)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = RadioComponentAdapter(RadioComponentListener { componentId ->
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToRadioComponentDetailsFragment(componentId, args.query, false)
            )
        })

        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID, NO_ID_SET, getString(R.string.add_component), args.query, false
                )
            )
        })

        return binding.root
    }
}