package com.sergeyrodin.matchesboxes.bag.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.matchesboxes.ADD_NEW_ITEM_ID
import com.sergeyrodin.matchesboxes.EventObserver
import com.sergeyrodin.matchesboxes.MatchesBoxesApplication
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.databinding.FragmentBagsListBinding

/**
 * A simple [Fragment] subclass.
 */
class BagsListFragment : Fragment() {
    private lateinit var viewModel: BagsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = createBinding(inflater)
        viewModel = createViewModel()
        setupBinding(binding)
        observeAddBagEvent()
        observeSelectBagEvent()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun createBinding(inflater: LayoutInflater): FragmentBagsListBinding {
        return FragmentBagsListBinding.inflate(inflater)
    }

    private fun createViewModel(): BagsListViewModel {
        val viewModel by viewModels<BagsListViewModel> {
            BagsListViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }
        return viewModel
    }

    private fun setupBinding(
        binding: FragmentBagsListBinding
    ) {
        binding.bagsList.adapter = createAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun createAdapter(): DisplayQuantityAdapter {
        return DisplayQuantityAdapter(DisplayQuantityListener { bagId ->
            selectBagCallback(bagId)
        }, R.drawable.ic_bag)
    }

    private fun selectBagCallback(bagId: Int) {
        viewModel.selectBag(bagId)
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
            navigateToAddBagFragment()
        })
    }

    private fun navigateToAddBagFragment() {
        findNavController().navigate(
            BagsListFragmentDirections.actionBagsListFragmentToAddEditDeleteBagFragment(
                ADD_NEW_ITEM_ID, getString(R.string.add_bag)
            )
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_buy_menu, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object: OnQueryTextListener{
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

        })
    }

    private fun navigateToSearchFragment(query: String?) {
        findNavController().navigate(
            BagsListFragmentDirections.actionBagsListFragmentToSearchFragment(query!!)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_buy){
            navigateToNeededComponentsFragment()
            return true
        }
        if(item.itemId == R.id.action_history){
            navigateToHistoryFragment()
            return true
        }
        if(item.itemId == R.id.action_popular) {
            navigateToPopularFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToPopularFragment() {
        findNavController().navigate(
            BagsListFragmentDirections.actionBagsListFragmentToPopularComponentsFragment()
        )
    }

    private fun navigateToHistoryFragment() {
        findNavController().navigate(
            BagsListFragmentDirections
                .actionBagsListFragmentToHistoryAllFragment()
        )
    }

    private fun navigateToNeededComponentsFragment() {
        findNavController().navigate(
            BagsListFragmentDirections.actionBagsListFragmentToNeededComponentsFragment()
        )
    }

}
