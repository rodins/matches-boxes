package com.sergeyrodin.matchesboxes

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.data.ItemWithQuantityPresentation
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.history.HistoryPresentation
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationAdapter
import com.sergeyrodin.matchesboxes.history.component.DisplayComponentHistoryAdapter
import com.sergeyrodin.matchesboxes.popular.PopularPresentation
import com.sergeyrodin.matchesboxes.popular.PopularPresentationAdapter

@BindingAdapter("displayQuantityListData")
fun bindBagRecyclerView(recyclerView: RecyclerView, list: List<ItemWithQuantityPresentation>?) {
    val adapter = recyclerView.adapter as DisplayQuantityAdapter
    adapter.submitList(list)
}

@BindingAdapter("radioComponentsListData")
fun bindRadioComponentRecyclerView(recyclerView: RecyclerView, list: List<RadioComponent>?) {
    val adapter = recyclerView.adapter as RadioComponentAdapter
    adapter.submitList(list)
}

@BindingAdapter("displayHistoryListData")
fun bindDisplayHistoryListRecyclerView(
    recyclerView: RecyclerView,
    list: List<HistoryPresentation>?
) {
    val adapter = recyclerView.adapter as HistoryPresentationAdapter
    adapter.submitList(list)
}

@BindingAdapter("displayComponentHistoryListData")
fun bindDisplayComponentHistoryListRecyclerView(
    recyclerView: RecyclerView,
    list: List<HistoryPresentation>?
) {
    val adapter = recyclerView.adapter as DisplayComponentHistoryAdapter
    adapter.submitList(list)
}

@BindingAdapter("popularPresentationListData")
fun bindPopularPresentationListRecyclerView(recyclerView: RecyclerView, list: List<PopularPresentation>?) {
    val adapter = recyclerView.adapter as PopularPresentationAdapter
    adapter.submitList(list)
}

@BindingAdapter("noDataTextVisible")
fun bindNoDataViewVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("noDataViewHidden")
fun bindNoDataViewHidden(view: View, hidden: Boolean) {
    view.visibility = if (hidden) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("quantityInteger")
fun bindQuantityTextView(textView: TextView, quantity: Int) {
    textView.text = quantity.toString()
}
