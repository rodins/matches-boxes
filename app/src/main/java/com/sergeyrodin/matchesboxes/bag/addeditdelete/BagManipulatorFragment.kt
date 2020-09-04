package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.databinding.FragmentBagManipulatorBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class BagManipulatorFragment : Fragment() {
    private val viewModel by viewModels<BagManupulatorViewModel> {
        BagManipulatorViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private var isActionDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBagManipulatorBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<BagManipulatorFragmentArgs>()
        isActionDeleteVisible = args.bagId != ADD_NEW_ITEM_ID

        viewModel.start(args.bagId)

        viewModel.eventAdded.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(requireContext(), R.string.bag_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
            )
        })

        viewModel.eventEdited.observe(viewLifecycleOwner, EventObserver{ title ->
            hideKeyboard(activity)
            Toast.makeText(requireContext(), R.string.bag_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToMatchesBoxSetsListFragment(args.bagId, title)
            )
        })

        viewModel.eventDeleted.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                BagManipulatorFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = isActionDeleteVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteBag()
        }
        return super.onOptionsItemSelected(item)
    }

}
