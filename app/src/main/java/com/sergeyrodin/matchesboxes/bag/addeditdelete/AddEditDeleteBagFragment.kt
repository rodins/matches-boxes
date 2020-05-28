package com.sergeyrodin.matchesboxes.bag.addeditdelete

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication

import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentAddEditDeleteBagBinding

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

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val args by navArgs<AddEditDeleteBagFragmentArgs>()
        isActionDeleteVisible = args.id != ADD_NEW_ITEM_ID
        viewModel.start(args.id)

        binding.saveBagFab.setOnClickListener {
            viewModel.saveBag(binding.bagEdit.text.toString())
        }

        viewModel.eventAdded.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_added, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                AddEditDeleteBagFragmentDirections.actionAddEditDeleteBagFragmentToBagsListFragment()
            )
        })

        viewModel.eventEdited.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), R.string.bag_updated, Toast.LENGTH_SHORT).show()
            //TODO: add navigation maybe to MatcheboxesSetList
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