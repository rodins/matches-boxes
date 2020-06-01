package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
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

        val args by navArgs<AddEditDeleteMatchesBoxFragmentArgs>()
        viewModel.start(args.setId, args.boxId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.saveBoxFab.setOnClickListener{
            viewModel.saveMatchesBox(binding.boxEdit.text.toString())
        }

        viewModel.addEvent.observe(viewLifecycleOwner, EventObserver{
            // TODO: add toast message
            findNavController().navigate(
                AddEditDeleteMatchesBoxFragmentDirections
                    .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(args.setId)
            )
        })

        viewModel.deleteEvent.observe(viewLifecycleOwner, EventObserver{
            //TODO: add toast message
            findNavController().navigate(
                AddEditDeleteMatchesBoxFragmentDirections
                    .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(args.setId)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        //TODO: hide menu item in add item mode
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBox()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}