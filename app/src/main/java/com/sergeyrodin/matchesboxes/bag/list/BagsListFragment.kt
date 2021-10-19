package com.sergeyrodin.matchesboxes.bag.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagsListFragment : Fragment() {
    private val viewModel: BagsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_bags_list, container, false)
        view.findViewById<ComposeView>(R.id.compose_view).setContent {
            AppCompatTheme {
                BagsScreen(viewModel)
            }
        }

        observeAddBagEvent()
        observeSelectBagEvent()

        setHasOptionsMenu(true)
        return view
    }

    private fun observeSelectBagEvent() {
        viewModel.selectBagEvent.observe(viewLifecycleOwner, EventObserver { bag ->
            navigateToMatchesBoxSetListFragment(bag)
        })
    }

    private fun navigateToMatchesBoxSetListFragment(bag: Bag) {
        findNavController().navigate(
            BagsListFragmentDirections.actionBagsListFragmentToMatchesBoxSetsListFragment(
                bag.id,
                bag.name
            )
        )
    }

    private fun observeAddBagEvent() {
        viewModel.addBagEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddBagFragmentNoException()
        })
    }

    private fun navigateToAddBagFragmentNoException() {
        try {
            navigateToAddBagFragment()
        } catch (e: IllegalArgumentException) {
            showNavigationErrorToast()
        }
    }

    private fun navigateToAddBagFragment() {
        findNavController().navigate(
            BagsListFragmentDirections.actionBagsListFragmentToAddEditDeleteBagFragment(
                ADD_NEW_ITEM_ID, getString(R.string.add_bag)
            )
        )
    }

    private fun showNavigationErrorToast() {
        Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_buy_menu, menu)
        val item = menu.findItem(R.id.action_search)
        setupSearchView(item)
    }

    private fun setupSearchView(item: MenuItem) {
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(createOnQueryTextListener(searchView))
    }

    private fun createOnQueryTextListener(searchView: SearchView): OnQueryTextListener {
        return object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                navigateToSearchFragment(query)

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

    private fun navigateToSearchFragment(query: String?) {
        findNavController().navigate(
            BagsListFragmentDirections
                .actionBagsListFragmentToSearchFragment().setQuery(query!!)
        )
    }
}
