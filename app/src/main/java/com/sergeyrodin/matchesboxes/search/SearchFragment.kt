package com.sergeyrodin.matchesboxes.search

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.component.addeditdelete.NO_ID_SET
import com.sergeyrodin.matchesboxes.component.addeditdelete.RadioComponentManipulatorReturns
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentListener
import com.sergeyrodin.matchesboxes.databinding.FragmentSearchBinding
import java.lang.IllegalArgumentException

const val KEY_QUERY = "query"

class SearchFragment : Fragment() {
    private val viewModel by viewModels<SearchViewModel> {
        createViewModelFactory()
    }

    private fun createViewModelFactory(): SearchViewModelFactory {
        return SearchViewModelFactory(
            (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
        )
    }

    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getSearchQueryFromArgs()
        val binding = createBinding(inflater, container)
        startSearch()
        setupBinding(binding)
        observeAddComponentEvent()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun getSearchQueryFromArgs() {
        searchQuery = requireArguments().getString(KEY_QUERY, "")
    }

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchBinding.inflate(inflater, container, false)

    private fun startSearch() {
        viewModel.start(searchQuery)
    }

    private fun setupBinding(binding: FragmentSearchBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = createAdapter()
    }

    private fun createAdapter(): RadioComponentAdapter {
        return RadioComponentAdapter(RadioComponentListener { componentId ->
            navigateToRadioComponentDetailsFragment(componentId)
        })
    }

    private fun navigateToRadioComponentDetailsFragment(componentId: Int) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToRadioComponentDetailsFragment(
                componentId,
                searchQuery,
                RadioComponentManipulatorReturns.TO_SEARCH_LIST
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
            navigateToAddComponentFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToAddComponentFragment() {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToAddEditDeleteRadioComponentFragment(
                ADD_NEW_ITEM_ID,
                NO_ID_SET,
                getString(R.string.add_component),
                searchQuery,
                RadioComponentManipulatorReturns.TO_SEARCH_LIST
            )
        )
    }

    private fun showNavigationErrorToast() {
        Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.app_bar_search)
        setupSearchView(item)
    }

    private fun setupSearchView(item: MenuItem) {
        val searchView = item.actionView as SearchView
        setSearchViewQuery(searchView)
        setSearchViewParamsAndListener(searchView)
    }

    private fun setSearchViewQuery(searchView: SearchView) {
        if (searchView.query.isEmpty()) {
            searchView.setQuery(searchQuery, false)
        }
    }

    private fun setSearchViewParamsAndListener(searchView: SearchView) {
        searchView.apply {
            setIconifiedByDefault(false)
            isSubmitButtonEnabled = true
            setOnQueryTextListener(createOnQueryTextListener(searchView))
        }
    }

    private fun createOnQueryTextListener(searchView: SearchView): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchQuery = it
                    saveQueryToArgs()
                    startSearch()
                }
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        }
    }

    private fun saveQueryToArgs() {
        val bundle = requireArguments()
        bundle.putString(KEY_QUERY, searchQuery)
        arguments = bundle
    }
}