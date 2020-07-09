package com.sergeyrodin.matchesboxes.searchbuy.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentSearchBuyEditBinding

class SearchBuyEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBuyEditBinding.inflate(inflater)
        val viewModel by viewModels<SearchBuyEditViewModel>{
            SearchBuyEditViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<SearchBuyEditFragmentArgs>()
        viewModel.start(args.componentId)

        viewModel.saveItemEvent.observe(viewLifecycleOwner, EventObserver{
            val title = if(args.isSearch) {
                getString(R.string.search_components)
            }else{
                getString(R.string.buy_components)
            }
            findNavController().navigate(
                SearchBuyEditFragmentDirections
                    .actionSearchBuyEditFragmentToSearchBuyFragment(args.query, args.isSearch, title)
            )
        })

        return binding.root
    }
}