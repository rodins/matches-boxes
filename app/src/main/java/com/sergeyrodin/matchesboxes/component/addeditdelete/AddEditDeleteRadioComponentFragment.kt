package com.sergeyrodin.matchesboxes.component.addeditdelete

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
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteRadioComponentBinding

class AddEditDeleteRadioComponentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteRadioComponentBinding.inflate(inflater)
        val viewModel by viewModels<AddEditDeleteRadioComponentViewModel> {
            AddEditDeleteRadioComponentViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        val args by navArgs<AddEditDeleteRadioComponentFragmentArgs>()
        viewModel.start(args.boxId, args.componentId)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.saveComponentFab.setOnClickListener {
            viewModel.saveItem(
                binding.componentEdit.text.toString(),
                binding.quantityEdit.text.toString())
        }

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{
            //TODO: add toast
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        viewModel.updateItemEvent.observe(viewLifecycleOwner, EventObserver{
            //TODO: add toast
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        return binding.root
    }

}