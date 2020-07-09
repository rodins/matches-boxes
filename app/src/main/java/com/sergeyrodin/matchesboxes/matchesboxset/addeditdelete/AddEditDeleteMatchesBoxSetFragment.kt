package com.sergeyrodin.matchesboxes.matchesboxset.addeditdelete

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteMatchesBoxSetBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

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
        isDeleteVisible = args.setId != ADD_NEW_ITEM_ID

        viewModel.start(args.bagId, args.setId)

        viewModel.addedEvent.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(context, R.string.matches_box_set_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(args.bagId)
            )
        })

        viewModel.updatedEvent.observe(viewLifecycleOwner, EventObserver{ set ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.matches_box_set_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxListFragment(args.setId, set.name)
            )
        })

        viewModel.deletedEvent.observe(viewLifecycleOwner, EventObserver{ bagId ->
            Toast.makeText(context, R.string.matches_box_set_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteMatchesBoxSetFragmentDirections
                    .actionAddEditDeleteMatchesBoxSetFragmentToMatchesBoxSetsListFragment(bagId)
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
