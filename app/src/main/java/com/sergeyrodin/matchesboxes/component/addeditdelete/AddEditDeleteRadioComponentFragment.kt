package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteRadioComponentBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class AddEditDeleteRadioComponentFragment : Fragment() {
    private val viewModel by viewModels<AddEditDeleteRadioComponentViewModel> {
        AddEditDeleteRadioComponentViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }
    private var isDeleteVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteRadioComponentBinding.inflate(inflater)

        val args by navArgs<AddEditDeleteRadioComponentFragmentArgs>()
        isDeleteVisible = args.componentId != ADD_NEW_ITEM_ID

        if(activity is MainActivity) {
            if(isDeleteVisible) {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.update_component)
            }else {
                (activity as MainActivity).supportActionBar?.title = getString(R.string.add_component)
            }
        }
        
        viewModel.start(args.boxId, args.componentId)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(context, R.string.component_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        viewModel.updateItemEvent.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(activity)
            Toast.makeText(context, R.string.component_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        viewModel.deleteItemEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, R.string.component_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
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
            viewModel.deleteItem()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}