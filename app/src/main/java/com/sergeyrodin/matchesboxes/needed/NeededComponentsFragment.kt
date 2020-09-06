package com.sergeyrodin.matchesboxes.needed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentListener
import com.sergeyrodin.matchesboxes.databinding.FragmentNeededComponentsBinding

class NeededComponentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNeededComponentsBinding.inflate(inflater, container, false)
        val viewModel by viewModels<NeededComponentsViewModel> {
            NeededComponentsViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = RadioComponentAdapter(RadioComponentListener { componentId ->
            findNavController().navigate(
                NeededComponentsFragmentDirections.actionNeededComponentsFragmentToRadioComponentDetailsFragment(componentId, "", true)
            )
        })

        viewModel.addComponentEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                NeededComponentsFragmentDirections.actionNeededComponentsFragmentToAddEditDeleteRadioComponentFragment(
                    ADD_NEW_ITEM_ID, NO_ID_SET, getString(R.string.add_component), "", true
                )
            )
        })

        return binding.root
    }
}