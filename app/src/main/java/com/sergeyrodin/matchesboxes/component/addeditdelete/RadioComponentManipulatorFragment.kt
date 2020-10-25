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
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentManipulatorBinding
import com.sergeyrodin.matchesboxes.util.hideKeyboard

class RadioComponentManipulatorFragment : Fragment() {
    private val viewModel by viewModels<RadioComponentManipulatorViewModel> {
        getViewModelFactory()
    }
    private val args by navArgs<RadioComponentManipulatorFragmentArgs>()

    private fun getViewModelFactory(): RadioComponentManipulatorViewModelFactory {
        return RadioComponentManipulatorViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private lateinit var binding: FragmentRadioComponentManipulatorBinding
    private var isDeleteVisible = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = createBinding(inflater)
        setupIsDeleteVisible()
        startViewModel()
        setupBinding()
        setupSpinners()
        setupManipulatorEvents()
        observeSavingErrorEvent()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater): FragmentRadioComponentManipulatorBinding {
        return FragmentRadioComponentManipulatorBinding.inflate(inflater)
    }

    private fun setupIsDeleteVisible() {
        isDeleteVisible = args.componentId != ADD_NEW_ITEM_ID
    }

    private fun startViewModel() {
        viewModel.start(args.boxId, args.componentId)
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupSpinners() {
        setupBoxSpinner()
        setupSetSpinner()
        setupBagSpinner()
    }

    private fun setupBoxSpinner() {
        observeBoxNames()
        observeBoxSelectedIndex()
    }

    private fun observeBoxNames() {
        viewModel.boxNames.observe(viewLifecycleOwner, Observer { boxNames ->
            setAdapterForBoxes(createArrayAdapter(boxNames))
            setItemSelectedListenerForBoxes()
        })
    }

    private fun createArrayAdapter(
        names: List<String>
    ): ArrayAdapter<String> {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            names
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun setAdapterForBoxes(adapter: ArrayAdapter<String>) {
        binding.boxesSpinner.adapter = adapter
    }

    private fun setItemSelectedListenerForBoxes() {
        binding.boxesSpinner.onItemSelectedListener = getItemSelectedListener { position ->
            viewModel.boxSelected(position)
        }
    }

    private fun getItemSelectedListener(itemSelectedCallback: (Int) -> Unit): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                itemSelectedCallback(position)
            }

        }
    }

    private fun observeBoxSelectedIndex() {
        viewModel.boxSelectedIndex.observe(viewLifecycleOwner, Observer { index ->
            setBoxesSpinnerSelection(index)
        })
    }

    private fun setBoxesSpinnerSelection(index: Int) {
        binding.boxesSpinner.setSelection(index)
    }

    private fun setupSetSpinner() {
        observeSetNames()
        observeSetSelectedIndex()
    }

    private fun observeSetNames() {
        viewModel.setNames.observe(viewLifecycleOwner, Observer { setNames ->
            setAdapterForSets(createArrayAdapter(setNames))
            setItemSelectedListenerForSets()
        })
    }

    private fun setAdapterForSets(adapter: ArrayAdapter<String>) {
        binding.setsSpinner.adapter = adapter
    }

    private fun setItemSelectedListenerForSets() {
        binding.setsSpinner.onItemSelectedListener = getItemSelectedListener { position ->
            viewModel.setOfBoxesSelected(position)
        }
    }

    private fun observeSetSelectedIndex() {
        viewModel.setSelectedIndex.observe(viewLifecycleOwner, Observer { index ->
            setSetsSpinnerSelection(index)
        })
    }

    private fun setSetsSpinnerSelection(index: Int) {
        binding.setsSpinner.setSelection(index)
    }

    private fun setupBagSpinner() {
        observeBagNames()
        observeBagSelectedIndex()
    }

    private fun observeBagNames() {
        viewModel.bagNames.observe(viewLifecycleOwner, Observer { bagNames ->
            setBagsAdapter(createArrayAdapter(bagNames))
            setItemSelectedListenerForBags()
        })
    }

    private fun setBagsAdapter(adapter: ArrayAdapter<String>) {
        binding.bagsSpinner.adapter = adapter
    }

    private fun setItemSelectedListenerForBags() {
        binding.bagsSpinner.onItemSelectedListener = getItemSelectedListener { position ->
            viewModel.bagSelected(position)
        }
    }

    private fun observeBagSelectedIndex() {
        viewModel.bagSelectedIndex.observe(viewLifecycleOwner, Observer { index ->
            setSelectionForBagsSpinner(index)
        })
    }

    private fun setSelectionForBagsSpinner(index: Int?) {
        index?.let {
            binding.bagsSpinner.setSelection(index)
        }
    }

    private fun setupManipulatorEvents() {
        observeAddEvent()
        observeUpdateEvent()
        observeDeleteEvent()
    }

    private fun observeAddEvent() {
        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver { box ->
            hideKeyboard(activity)
            makeToastForAddEvent()
            if (args.isBuy || args.query.isNotEmpty()) {
                navigateInSearchBuyMode()
            } else {
                navigateToRadioComponentsListFragment(box)
            }
        })
    }

    private fun makeToastForAddEvent() {
        Toast.makeText(context, R.string.component_added, Toast.LENGTH_SHORT).show()
    }

    private fun navigateInSearchBuyMode() {
        if (args.isBuy) {
            navigateToNeededComponentsFragment()
        } else {
            navigateToSearchFragment()
        }
    }

    private fun navigateToNeededComponentsFragment() {
        findNavController().navigate(
            RadioComponentManipulatorFragmentDirections.actionAddEditDeleteRadioComponentFragmentToNeededComponentsFragment()
        )
    }

    private fun navigateToSearchFragment() {
        findNavController().navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToSearchFragment().setQuery(args.query)
        )
    }

    private fun navigateToRadioComponentsListFragment(box: MatchesBox) {
        findNavController().navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    box.id,
                    box.name
                )
        )
    }

    private fun observeUpdateEvent() {
        viewModel.updateItemEvent.observe(viewLifecycleOwner, EventObserver { box ->
            hideKeyboard(activity)
            makeToastForUpdatedEvent()
            if (args.isBuy || args.query.isNotEmpty()) {
                navigateInSearchBuyMode()
            } else {
                navigateToRadioComponentsListFragment(box)
            }
        })
    }

    private fun makeToastForUpdatedEvent() {
        Toast.makeText(context, R.string.component_updated, Toast.LENGTH_SHORT).show()
    }


    private fun observeDeleteEvent() {
        viewModel.deleteItemEvent.observe(viewLifecycleOwner, EventObserver { title ->
            makeToastForDeleteEvent()
            if (args.isBuy || args.query.isNotEmpty()) {
                navigateInSearchBuyMode()
            } else {
                navigateToRadioComponentsListFragment(title)
            }
        })
    }

    private fun makeToastForDeleteEvent() {
        Toast.makeText(context, R.string.component_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRadioComponentsListFragment(title: String) {
        findNavController().navigate(
            RadioComponentManipulatorFragmentDirections
                .actionAddEditDeleteRadioComponentFragmentToRadioComponentsListFragment(
                    args.boxId,
                    title
                )
        )
    }

    private fun observeSavingErrorEvent() {
        viewModel.savingErrorEvent.observe(viewLifecycleOwner, EventObserver {
            makeToastForErrorEvent()
        })
    }

    private fun makeToastForErrorEvent() {
        Toast.makeText(requireContext(), R.string.save_component_error, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
        setupActionDeleteVisibility(menu)
    }

    private fun setupActionDeleteVisibility(menu: Menu) {
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = isDeleteVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            viewModel.deleteItem()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}