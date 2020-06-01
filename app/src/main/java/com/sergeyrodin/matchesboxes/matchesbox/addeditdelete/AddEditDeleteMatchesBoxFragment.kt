package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteMatchesBoxBinding

class AddEditDeleteMatchesBoxFragment : Fragment() {
    private val viewModel by viewModels<AddEditDeleteMatchesBoxViewModel> {
        AddEditDeleteMatchesBoxViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteMatchesBoxBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val args by navArgs<AddEditDeleteMatchesBoxFragmentArgs>()
        viewModel.start(args.setId, args.boxId)

        binding.saveBoxFab.setOnClickListener{
            viewModel.saveMatchesBox(binding.boxEdit.text.toString())
        }

        return binding.root
    }
}