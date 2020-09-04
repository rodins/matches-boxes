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
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentManipulatorBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class RadioComponentManipulatorFragment : Fragment() {
    private val viewModel by viewModels<RadioComponentManipulatorViewModel> {
        RadioComponentManipulatorViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }
    private var isDeleteVisible = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRadioComponentManipulatorBinding.inflate(inflater)

        val args by navArgs<RadioComponentManipulatorFragmentArgs>()
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
                    binding.boxesSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.boxSelected(position)
                        }

                    }
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
                    binding.setsSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.setSelected(position)
                        }
                    }
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
                    binding.bagsSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                           viewModel.bagSelected(position)
                        }
                    }
                }
            }
        })

        viewModel.bagSelectedIndex.observe(viewLifecycleOwner, Observer{ index ->
            index?.let{
                binding.bagsSpinner.setSelection(index)
            }
        })

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{ box ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.component_added, Toast.LENGTH_SHORT).show()
            if(args.isBuy || args.query.isNotEmpty()) {
                findNavController().navigate(
                    RadioComponentManipulatorFragmentDirections
                        .actionAddEditDeleteRadioComponentFragmentToSearchBuyFragment(args.query, !args.isBuy,
                            if(args.isBuy)
                                getString(R.string.buy_components)
                            else
                                getString(R.string.search_components)
                        )
                )
            }else {
                findNavController().navigate(
                    RadioComponentManipulatorFragmentDirections
                        .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(box.id, box.name)
                )
            }
        })

        viewModel.updateItemEvent.observe(viewLifecycleOwner, EventObserver{ box ->
            hideKeyboard(activity)
            Toast.makeText(context, R.string.component_updated, Toast.LENGTH_SHORT).show()
            if(args.isBuy || args.query.isNotEmpty()) {
                findNavController().navigate(
                    RadioComponentManipulatorFragmentDirections
                        .actionAddEditDeleteRadioComponentFragmentToSearchBuyFragment(args.query, !args.isBuy,
                            if(args.isBuy)
                                getString(R.string.buy_components)
                            else
                                getString(R.string.search_components)
                        )
                )
            }else {
                findNavController().navigate(
                    RadioComponentManipulatorFragmentDirections
                        .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(box.id, box.name)
                )
            }
        })

        viewModel.deleteItemEvent.observe(viewLifecycleOwner, EventObserver{ title ->
            Toast.makeText(context, R.string.component_deleted, Toast.LENGTH_SHORT).show()
            if(args.isBuy || args.query.isNotEmpty()) {
                findNavController().navigate(
                    RadioComponentManipulatorFragmentDirections
                        .actionAddEditDeleteRadioComponentFragmentToSearchBuyFragment(args.query, !args.isBuy,
                            if(args.isBuy)
                                getString(R.string.buy_components)
                            else
                                getString(R.string.search_components)
                        )
                )
            }else {
                findNavController().navigate(
                    RadioComponentManipulatorFragmentDirections
                        .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(args.boxId, title)
                )
            }
        })

        viewModel.savingErrorEvent.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.save_component_error, Toast.LENGTH_SHORT).show()
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