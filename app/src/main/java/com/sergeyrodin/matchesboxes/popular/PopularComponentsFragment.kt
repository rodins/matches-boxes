package com.sergeyrodin.matchesboxes.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sergeyrodin.matchesboxes.databinding.FragmentPopularComponentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularComponentsFragment : Fragment() {

    private val viewModel by viewModels<PopularComponentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = createBinding(inflater, container)
        setupBinding(binding, viewModel)
        return binding.root
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPopularComponentsBinding.inflate(inflater, container, false)

    private fun setupBinding(
        binding: FragmentPopularComponentsBinding,
        viewModel: PopularComponentsViewModel
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.items.adapter = PopularPresentationAdapter()
        binding.viewModel = viewModel
    }
}