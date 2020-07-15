package com.sergeyrodin.matchesboxes.component.addeditdelete

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteRadioComponentBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class AddEditDeleteRadioComponentFragment : Fragment(), AdapterView.OnItemSelectedListener {
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

        viewModel.start(args.boxId, args.componentId)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.boxNames.observe(viewLifecycleOwner, Observer{ boxNames ->
            context?.let{
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    boxNames
                ).also{ adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.boxesSpinner.adapter = adapter
                    binding.boxesSpinner.onItemSelectedListener = this
                }
            }
        })

        viewModel.boxSelectedIndex.observe(viewLifecycleOwner, Observer { index ->
            index?.let{
                binding.boxesSpinner.setSelection(index)
            }
        })

        viewModel.setNames.observe(viewLifecycleOwner, Observer{ setNames ->
            context?.let{
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    setNames
                ).also{ adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.setsSpinner.adapter = adapter
                    binding.setsSpinner.onItemSelectedListener = this
                }
            }
        })

        viewModel.setSelectedIndex.observe(viewLifecycleOwner, Observer{ index ->
            index?.let{
                binding.setsSpinner.setSelection(index)
            }
        })

        viewModel.bagNames.observe(viewLifecycleOwner, Observer{ bagNames ->
            context?.let{
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    bagNames
                ).also{ adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.bagsSpinner.adapter = adapter
                    binding.bagsSpinner.onItemSelectedListener = this
                }
            }
        })

        viewModel.bagSelectedIndex.observe(viewLifecycleOwner, Observer{ index ->
            index?.let{
                binding.bagsSpinner.setSelection(index)
            }
        })

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{ title ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.component_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId, title)
            )
        })

        viewModel.updateItemEvent.observe(viewLifecycleOwner, EventObserver{ title ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.component_updated, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId, title)
            )
        })

        viewModel.deleteItemEvent.observe(viewLifecycleOwner, EventObserver{ title ->
            Toast.makeText(context, R.string.component_deleted, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteRadioComponentFragmentDirections
                    .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId, title)
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

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        //TODO("Not yet implemented")
    }

}