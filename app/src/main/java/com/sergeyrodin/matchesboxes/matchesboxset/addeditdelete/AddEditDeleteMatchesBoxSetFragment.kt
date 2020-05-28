package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteMatchesBoxSetBinding
import java.util.*

class AddEditDeleteMatchesBoxSetFragment : Fragment() {

    val viewModel by viewModels<AddEditDeleteMatchesBoxSetViewModel> {
        AddEditDeleteMatchBoxSetViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteMatchesBoxSetBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<AddEditDeleteMatchesBoxSetFragmentArgs>()
        viewModel.start(args.bagId, args.matchesBoxSetId)

        binding.saveSetFab.setOnClickListener {
            viewModel.saveMatchesBoxSet(binding.setEdit.text.toString())
        }

        viewModel.addedEvent.observe(viewLifecycleOwner, EventObserver{
            // TODO: uncomment toast when able to test it
            //Toast.makeText(context, R.string.matches_box_set_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(args.bagId)
            )
        })

        // TODO: add update toast and navigation

        viewModel.deletedEvent.observe(viewLifecycleOwner, EventObserver{
            // TODO: add toast
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(args.bagId)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        //TODO: make menu item disappear when adding new item
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBoxSet()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
