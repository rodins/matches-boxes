package com.sergeyrodin.matchesboxes

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.data.HistoryModel
import com.sergeyrodin.matchesboxes.data.QuantityItemModel
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.history.all.HistoryModelAdapter
import com.sergeyrodin.matchesboxes.history.component.HistoryAdapter
import com.sergeyrodin.matchesboxes.popular.PopularPresentation
import com.sergeyrodin.matchesboxes.popular.PopularPresentationAdapter
import com.sergeyrodin.matchesboxes.util.convertLongToDateString
import com.sergeyrodin.matchesboxes.util.getDeltaById
import com.sergeyrodin.matchesboxes.util.getHighlightedBackgroundById

@BindingAdapter("displayQuantityListData")
fun bindBagRecyclerView(recyclerView: RecyclerView, list: List<QuantityItemModel>?) {
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
    list: List<HistoryModel>?
) {
    val adapter = recyclerView.adapter as HistoryModelAdapter
    adapter.submitList(list)
}

@BindingAdapter("displayComponentHistoryListData")
fun bindDisplayComponentHistoryListRecyclerView(
    recyclerView: RecyclerView,
    list: List<History>?
) {
    val adapter = recyclerView.adapter as HistoryAdapter
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

@BindingAdapter("dateText")
fun bindDateToTextView(textView: TextView, date: Long) {
    textView.text = convertLongToDateString(date)
}

@BindingAdapter("delta")
fun bindDeltaToTextView(textView: TextView, id: Int) {
    textView.text = getDeltaById(id)
}

@BindingAdapter("highlightedId")
fun bindHighlightedIdToView(view: View, id: Int) {
    view.setBackgroundResource(getHighlightedBackgroundById(id))
}
