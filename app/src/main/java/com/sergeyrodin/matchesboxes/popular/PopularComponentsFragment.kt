package com.sergeyrodin.matchesboxes.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.databinding.FragmentPopularComponentsBinding

class PopularComponentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = createBinding(inflater, container)
        val viewModel = createViewModel()
        setupBinding(binding, viewModel)
        return binding.root
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPopularComponentsBinding.inflate(inflater, container, false)

    private fun createViewModel(): PopularComponentsViewModel {
        val viewModel by viewModels<PopularComponentsViewModel> {
            createViewModelFactory()
        }
        return viewModel
    }

    private fun createViewModelFactory(): PopularComponentsViewModelFactory {
        return PopularComponentsViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private fun setupBinding(
        binding: FragmentPopularComponentsBinding,
        viewModel: PopularComponentsViewModel
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.items.adapter = PopularPresentationAdapter()
        binding.viewModel = viewModel
    }
}