package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*

import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteBagBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class AddEditDeleteBagFragment : Fragment() {
    private val viewModel by viewModels<AddEditDeleteBagViewModel> {
        AddEditDeleteBagViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private var isActionDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteBagBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<AddEditDeleteBagFragmentArgs>()
        isActionDeleteVisible = args.bag != null

        if(activity is MainActivity) {
            if(isActionDeleteVisible) {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.update_bag)
            }else {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.add_bag)
            }
        }

        viewModel.start(args.bag?.id?: ADD_NEW_ITEM_ID)

        viewModel.eventAdded.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(requireContext(), R.string.bag_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteBagFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
            )
        })

        viewModel.eventEdited.observe(viewLifecycleOwner, EventObserver{bag ->
            hideKeyboard(activity)
            Toast.makeText(requireContext(), R.string.bag_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteBagFragmentDirections.actionAddEditDeleteBagFragmentToMatchesBoxSetsListFragment(bag)
            )
        })

        viewModel.eventDeleted.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteBagFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
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
