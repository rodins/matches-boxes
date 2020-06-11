package com.sergeyrodin.matchesboxes.matchesbox.addeditdelete

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteMatchesBoxBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class AddEditDeleteMatchesBoxFragment : Fragment() {
    private val viewModel by viewModels<AddEditDeleteMatchesBoxViewModel> {
        AddEditDeleteMatchesBoxViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private var hideDelete = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteMatchesBoxBinding.inflate(inflater)

        val args by navArgs<AddEditDeleteMatchesBoxFragmentArgs>()
        hideDelete = args.boxId != ADD_NEW_ITEM_ID

        viewModel.start(args.setId, args.boxId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.saveBoxFab.setOnClickListener{
            viewModel.saveMatchesBox(binding.boxEdit.text.toString())
            hideKeyboard(activity)
        }

        viewModel.addEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, R.string.box_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxFragmentDirections
                    .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(args.setId)
            )
        })

        viewModel.deleteEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, R.string.box_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxFragmentDirections
                    .actionAddEditDeleteMatchesBoxFragmentToMatchesBoxListFragment(args.setId)
            )
        })

        viewModel.updateEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, R.string.box_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxFragmentDirections
                    .actionAddEditDeleteMatchesBoxFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = hideDelete
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBox()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}