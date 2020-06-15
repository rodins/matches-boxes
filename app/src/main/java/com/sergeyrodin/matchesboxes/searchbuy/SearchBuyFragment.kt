package com.sergeyrodin.matchesboxes.searchbuy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.MainActivity
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentListener
import com.sergeyrodin.matchesboxes.databinding.FragmentSearchBuyBinding


class SearchBuyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBuyBinding.inflate(inflater)
        val viewModel by viewModels<SearchBuyViewModel> {
            SearchBuyViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        val args by navArgs<SearchBuyFragmentArgs>()

        if(args.isSearch) {
            (activity as MainActivity).supportActionBar?.title = getString(R.string.search_components)
        }else {
            (activity as MainActivity).supportActionBar?.title = getString(R.string.buy_components)
        }

        viewModel.start(args.query, args.isSearch)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = RadioComponentAdapter(RadioComponentListener {
            // TODO: implement navigation to AddEditRadioComponentFragment
        })

        return binding.root
    }
}