package com.sergeyrodin.matchesboxes.bag.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R

import com.sergeyrodin.matchesboxes.databinding.FragmentBagsListBinding

/**
 * A simple [Fragment] subclass.
 */
class BagsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBagsListBinding.inflate(inflater)
        val viewModel by viewModels<BagsListViewModel> {
            BagsListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource)
        }
        val adapter = BagAdapter(BagListener {
            viewModel.selectItem(it)
        })
        binding.bagsList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        //TODO: change to binding
        binding.addBagFab.setOnClickListener {
            viewModel.addItem()
        }

        viewModel.addItemEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                BagsListFragmentDirections.actionBagsListFragmentToAddEditDeleteBagFragment(ADD_NEW_ITEM_ID)
            )
        })

        viewModel.selectItemEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                BagsListFragmentDirections.actionBagsListFragmentToMatchesBoxSetsListFragment(it)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_buy_menu, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                findNavController().navigate(
                    BagsListFragmentDirections
                        .actionBagsListFragmentToSearchBuyFragment(query!!, true)
                )

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

}
