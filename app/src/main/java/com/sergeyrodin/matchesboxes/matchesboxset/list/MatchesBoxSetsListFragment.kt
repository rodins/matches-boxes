package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.matchesboxes.*
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityListener
import com.sergeyrodin.matchesboxes.common.list.CommonViewModel
import com.sergeyrodin.matchesboxes.common.list.CommonViewModelFactory
import com.sergeyrodin.matchesboxes.databinding.FragmentMatchesBoxSetsListBinding

/**
 * A simple [Fragment] subclass.
 */
class MatchesBoxSetsListFragment : Fragment() {

    private val args by navArgs<MatchesBoxSetsListFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMatchesBoxSetsListBinding.inflate(inflater)
        val viewModel by activityViewModels<CommonViewModel> {
            CommonViewModelFactory(
                (requireContext().applicationContext as MatchesBoxesApplication).radioComponentsDataSource
            )
        }

        viewModel.startSet(args.bagId)

        val adapter = DisplayQuantityAdapter(DisplayQuantityListener{
            viewModel.selectSet(it)
        })
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.items.adapter = adapter

        viewModel.bagTitle.observe(viewLifecycleOwner, Observer{title ->
            if(activity is AppCompatActivity) {
                (activity as AppCompatActivity).supportActionBar?.title = title
            }
        })

        viewModel.addSetEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToAddEditDeleteMatchesBoxSetFragment(
                        args.bagId, ADD_NEW_ITEM_ID, getString(R.string.add_set))
            )
        })

        viewModel.selectSetEvent.observe(viewLifecycleOwner, EventObserver{ id ->
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToMatchesBoxListFragment(id)
            )
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_edit) {
            findNavController().navigate(
                MatchesBoxSetsListFragmentDirections
                    .actionMatchesBoxSetsListFragmentToAddEditDeleteBagFragment(args.bagId, getString(R.string.update_bag))
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
