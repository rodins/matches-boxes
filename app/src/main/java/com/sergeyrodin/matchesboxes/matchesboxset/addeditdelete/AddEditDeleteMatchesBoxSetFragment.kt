package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MainActivity
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteMatchesBoxSetBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard
import java.util.*

class AddEditDeleteMatchesBoxSetFragment : Fragment() {

    private val viewModel by viewModels<AddEditDeleteMatchesBoxSetViewModel> {
        AddEditDeleteMatchBoxSetViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }
    private var isDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteMatchesBoxSetBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<AddEditDeleteMatchesBoxSetFragmentArgs>()
        isDeleteVisible = args.setId != -1

        if(activity is MainActivity) {
            if(isDeleteVisible) {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.update_set)
            }else {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.add_set)
            }
        }

        viewModel.start(args.bag.id, args.setId)

        viewModel.addedEvent.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(context, R.string.matches_box_set_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(args.bag)
            )
        })

        viewModel.updatedEvent.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(context, R.string.matches_box_set_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxListFragment(args.setId, args.bag)
            )
        })

        viewModel.deletedEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, R.string.matches_box_set_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(args.bag)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        val item = menu.findItem(R.id.action_delete)
        item.setVisible(isDeleteVisible)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBoxSet()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
