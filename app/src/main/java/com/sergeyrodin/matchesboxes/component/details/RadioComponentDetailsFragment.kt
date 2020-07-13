package com.sergeyrodin.matchesboxes.component.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentDetailsBinding

class RadioComponentDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRadioComponentDetailsBinding.inflate(inflater, container, false)

        val viewModel by viewModels<RadioComponentDetailsViewModel> {
            RadioComponentDetailsViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val args by navArgs<RadioComponentDetailsFragmentArgs>()

        viewModel.start(args.componentId)

        viewModel.editEvent.observe(viewLifecycleOwner, EventObserver{ component ->
            findNavController().navigate(RadioComponentDetailsFragmentDirections
                .actionRadioComponentDetailsFragmentToAddEditDeleteRadioComponentFragment(
                    component.id, component.matchesBoxId, getString(R.string.update_component)))
        })

        return binding.root
    }
}