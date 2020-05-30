package com.sergeyrodin.matchesboxes.matchesbox.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxListBinding

class MatchesBoxListFragment : Fragment() {
    private val viewModel by viewModels<MatchesBoxListViewModel> {
        MatchesBoxListViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchesBoxListBinding.inflate(inflater)
        val args by navArgs<MatchesBoxListFragmentArgs>()
        viewModel.start(args.setId)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = MatchesBoxAdapter(MatchesBoxListener{
            viewModel.selectMatchesBox(it)
        })

        binding.items.adapter = adapter

        return binding.root
    }

}
