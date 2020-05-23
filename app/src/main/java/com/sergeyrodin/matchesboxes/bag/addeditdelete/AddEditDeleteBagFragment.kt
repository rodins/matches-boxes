package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteBagBinding

class AddEditDeleteBagFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteBagBinding.inflate(inflater)
        val viewModel by viewModels<AddEditDeleteBagViewModel> {
            AddEditDeleteBagViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val args by navArgs<AddEditDeleteBagFragmentArgs>()
        viewModel.start(args.id)

        binding.saveBagFab.setOnClickListener {
            viewModel.saveBag(binding.bagEdit.text.toString())
        }

        viewModel.eventAdded.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteBagFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
            )
        })

        viewModel.eventEdited.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_updated, Toast.LENGTH_SHORT).show()
            //TODO: add navigation maybe to MatcheboxesSetList
        })

        viewModel.eventDeleted.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteBagFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
            )
        })

        return binding.root
    }

}
