package com.sergeyrodin.matchesboxes.bag.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentBagsListBinding

/**
 * A simple [Fragment] subclass.
 */
class BagsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBagsListBinding.inflate(inflater)
        val viewModel by viewModels<BagsListViewModel> {
            BagsListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource)
        }
        val adapter = BagAdapter(BagListener {
            viewModel.selectItem(it)
        })
        binding.bagsList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

}