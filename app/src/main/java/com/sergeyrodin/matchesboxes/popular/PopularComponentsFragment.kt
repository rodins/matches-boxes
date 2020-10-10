package com.sergeyrodin.matchesboxes.popular

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentPopularComponentsBinding

class PopularComponentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPopularComponentsBinding.inflate(inflater, container, false)
        val viewModel by viewModels<PopularComponentsViewModel> {
            PopularComponentsViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.items.adapter = PopularPresentationAdapter()
        binding.viewModel = viewModel
        return binding.root
    }

}