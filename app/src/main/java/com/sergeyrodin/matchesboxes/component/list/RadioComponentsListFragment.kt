package com.sergeyrodin.matchesboxes.component.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentsListBinding

class RadioComponentsListFragment : Fragment() {
    private val args by navArgs<RadioComponentsListFragmentArgs>()
    private val viewModel by viewModels<RadioComponentsListViewModel>{
        createViewModelFactory()
    }

    private fun createViewModelFactory(): RadioComponentsListViewModelFactory {
        return RadioComponentsListViewModelFactory(
            getDataSourceFromApplication()
        )
    }

    private fun getDataSourceFromApplication() =
        (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = createBinding(inflater)
        startViewModel()
        setupBinding(binding)
        observeAddComponentEvent()

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater) =
        FragmentRadioComponentsListBinding.inflate(inflater)

    private fun startViewModel() {
        viewModel.startComponent(args.boxId)
    }

    private fun setupBinding(binding: FragmentRadioComponentsListBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = createAdapter()
    }

    private fun createAdapter(): RadioComponentAdapter {
        return RadioComponentAdapter(RadioComponentListener { id ->
            navigateToDetailsFragment(id)
        })
    }

    private fun navigateToDetailsFragment(id: Int) {
        findNavController().navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToRadioComponentDetailsFragment(
                    id,
                    "",
                    RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                )
        )
    }

    private fun observeAddComponentEvent() {
        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddComponentNoException()
        })
    }

    private fun navigateToAddComponentNoException() {
        try {
            navigateToAddFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToAddFragment() {
        findNavController().navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID,
                    args.boxId,
                    getString(R.string.add_component),
                    "",
                    RadioComponentManipulatorReturns.TO_COMPONENTS_LIST
                )
        )
    }

    private fun showNavigationErrorToast() {
        Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_edit) {
            navigateToEditMatchesBoxFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToEditMatchesBoxFragment() {
        findNavController().navigate(
            RadioComponentsListFragmentDirections
                .actionRadioComponentsListFragmentToAddEditDeleteMatchesBoxFragment(
                    DO_NOT_NEED_THIS_VARIABLE, args.boxId, getString(R.string.update_box)
                )
        )
    }
}