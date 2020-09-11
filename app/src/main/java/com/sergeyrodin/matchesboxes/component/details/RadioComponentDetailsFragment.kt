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
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.databinding.FragmentRadioComponentDetailsBinding

class RadioComponentDetailsFragment : Fragment() {

    private val viewModel by viewModels<RadioComponentDetailsViewModel> {
        getViewModelFactory()
    }

    private fun getViewModelFactory(): RadioComponentDetailsViewModelFactory {
        return RadioComponentDetailsViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private lateinit var binding: FragmentRadioComponentDetailsBinding

    private val args by navArgs<RadioComponentDetailsFragmentArgs>()
    private var shouldInfoButtonBeDisplayed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createBinding(inflater, container)
        setupBinding()
        startViewModel()
        observeEditEvent()
        showInfoButtonIfSearchWebAppAvailable()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentRadioComponentDetailsBinding.inflate(inflater, container, false)
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun startViewModel() {
        viewModel.start(args.componentId)
    }

    private fun observeEditEvent() {
        viewModel.editEvent.observe(viewLifecycleOwner, EventObserver { component ->
            navigateToRadioComponentManipulatorFragment(component)
        })
    }

    private fun navigateToRadioComponentManipulatorFragment(component: RadioComponent) {
        findNavController().navigate(
            RadioComponentDetailsFragmentDirections
                .actionRadioComponentDetailsFragmentToAddEditDeleteRadioComponentFragment(
                    component.id,
                    component.matchesBoxId,
                    getString(R.string.update_component),
                    args.query,
                    args.isBuy
                )
        )
    }

    private fun showInfoButtonIfSearchWebAppAvailable() {
        if (null != getWebSearchIntent(
                getComponentName()
            ).resolveActivity(requireActivity().packageManager)
        ) {
            shouldInfoButtonBeDisplayed = true
        }
    }

    private fun getComponentName() = viewModel.details.value?.componentName ?: ""

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
        setupActionInfoVisibility(menu)
    }

    private fun setupActionInfoVisibility(menu: Menu) {
        val item = menu.findItem(R.id.action_info)
        item.isVisible = shouldInfoButtonBeDisplayed
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_info) {
            showInfoInExternalActivity()
            return true
        }
        if(item.itemId == R.id.action_history) {
            navigateToComponentHistoryFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInfoInExternalActivity() {
        startActivity(getWebSearchIntent(getComponentName()))
    }

    private fun getWebSearchIntent(query: String): Intent {
        return Intent(Intent.ACTION_WEB_SEARCH).putExtra(SearchManager.QUERY, query)
    }

    private fun navigateToComponentHistoryFragment() {
        findNavController().navigate(
            RadioComponentDetailsFragmentDirections
                .actionRadioComponentDetailsFragmentToComponentHistoryFragment(
                    args.componentId,
                    viewModel.details.value?.componentName ?: ""
                )
        )
    }
}