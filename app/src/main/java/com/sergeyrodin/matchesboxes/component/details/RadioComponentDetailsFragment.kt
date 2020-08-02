package com.sergeyrodin.matchesboxes.component.details

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentDetailsBinding

class RadioComponentDetailsFragment : Fragment() {

    private val viewModel by viewModels<RadioComponentDetailsViewModel> {
        RadioComponentDetailsViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private val args by navArgs<RadioComponentDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRadioComponentDetailsBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.start(args.componentId)

        viewModel.editEvent.observe(viewLifecycleOwner, EventObserver{ component ->
            findNavController().navigate(RadioComponentDetailsFragmentDirections
                .actionRadioComponentDetailsFragmentToAddEditDeleteRadioComponentFragment(
                    component.id, component.matchesBoxId, getString(R.string.update_component), args.query, args.isBuy))
        })

        if(null != getWebSearchIntent(viewModel.details.value?.componentName?:"").resolveActivity(requireActivity().packageManager)){
            setHasOptionsMenu(true)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_info) {
            startActivity(getWebSearchIntent(viewModel.details.value?.componentName?:""))
            return true
        }
        if(item.itemId == R.id.action_history) {
            findNavController().navigate(
                RadioComponentDetailsFragmentDirections
                    .actionRadioComponentDetailsFragmentToComponentHistoryFragment(
                        args.componentId,
                        viewModel.details.value?.componentName?:""
                    )
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getWebSearchIntent(query: String): Intent {
        return Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, query)
    }
}