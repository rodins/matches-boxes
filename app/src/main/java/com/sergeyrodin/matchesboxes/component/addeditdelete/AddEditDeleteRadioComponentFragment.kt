package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteRadioComponentBinding

class AddEditDeleteRadioComponentFragment : Fragment() {
    private val viewModel by viewModels<AddEditDeleteRadioComponentViewModel> {
        AddEditDeleteRadioComponentViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddEditDeleteRadioComponentBinding.inflate(inflater)

        val args by navArgs<AddEditDeleteRadioComponentFragmentArgs>()
        viewModel.start(args.boxId, args.componentId)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.saveComponentFab.setOnClickListener {
            viewModel.saveItem(
                binding.componentEdit.text.toString(),
                binding.quantityEdit.text.toString())
        }

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, R.string.component_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        viewModel.updateItemEvent.observe(viewLifecycleOwner, EventObserver{
            // TODO: add toast
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId)
            )
        })

        viewModel.deleteItemEvent.observe(viewLifecycleOwner, EventObserver{
            // TODO: add toast
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
        //TODO: hide button when add item
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.deleteItem()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}