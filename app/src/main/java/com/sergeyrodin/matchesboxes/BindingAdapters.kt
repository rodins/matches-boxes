package com.sergeyrodin.matchesboxes

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.bag.list.BagAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.matchesbox.list.MatchesBoxAdapter
import com.sergeyrodin.matchesboxes.matchesboxset.list.MatchesBoxSetAdapter
import com.sergeyrodin.matchesboxes.util.DisplayQuantity
import com.sergeyrodin.matchesboxes.util.MatchesBoxQuantity
import com.sergeyrodin.matchesboxes.util.MatchesBoxSetQuantity

@BindingAdapter("bagsListData")
fun bindBagRecyclerView(recyclerView: RecyclerView, list: List<DisplayQuantity>?) {
    val adapter = recyclerView.adapter as BagAdapter
    adapter.submitList(list)
}

@BindingAdapter("matchesBoxSetListData")
fun bindMatchesBoxSetRecyclerView(recyclerView: RecyclerView, list: List<MatchesBoxSetQuantity>?) {
    val adapter = recyclerView.adapter as MatchesBoxSetAdapter
    adapter.submitList(list)
}

@BindingAdapter("matchesBoxListData")
fun bindMatchesBoxRecyclerView(recyclerView: RecyclerView, list: List<MatchesBoxQuantity>?) {
    val adapter = recyclerView.adapter as MatchesBoxAdapter
    adapter.submitList(list)
}

@BindingAdapter("radioComponentsListData")
fun bindRadioComponentRecyclerView(recyclerView: RecyclerView, list: List<RadioComponent>?){
    val adapter = recyclerView.adapter as RadioComponentAdapter
    adapter.submitList(list)
}

@BindingAdapter("noDataTextVisible")
fun bindNoDataViewVisibility(view: View, visible: Boolean) {
    view.visibility = if(visible) View.VISIBLE else View.GONE
}

@BindingAdapter("quantityInteger")
fun bindQuantityTextView(textView: TextView, quantity: Int) {
    textView.text = quantity.toString()
}