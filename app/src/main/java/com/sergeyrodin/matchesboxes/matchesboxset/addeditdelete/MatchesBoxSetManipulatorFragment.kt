package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

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
import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxSetManipulatorBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class MatchesBoxSetManipulatorFragment : Fragment() {

    private val viewModel by viewModels<MatchesBoxSetManipulatorViewModel> {
        MatchBoxSetManipulatorViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }
    private var isDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchesBoxSetManipulatorBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<MatchesBoxSetManipulatorFragmentArgs>()
        isDeleteVisible = args.setId != ADD_NEW_ITEM_ID

        viewModel.start(args.bagId, args.setId)

        viewModel.addedEvent.observe(viewLifecycleOwner, EventObserver{ title ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.matches_box_set_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                MatchesBoxSetManipulatorFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(args.bagId, title)
            )
        })

        viewModel.updatedEvent.observe(viewLifecycleOwner, EventObserver{ set ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.matches_box_set_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                MatchesBoxSetManipulatorFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxListFragment(args.setId, set.name)
            )
        })

        viewModel.deletedEvent.observe(viewLifecycleOwner, EventObserver{ bag ->
            Toast.makeText(context, R.string.matches_box_set_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                MatchesBoxSetManipulatorFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(bag.id, bag.name)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = isDeleteVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteMatchesBoxSet()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
