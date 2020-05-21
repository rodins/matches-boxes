package com.sergeyrodin.matchesboxes

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.bag.list.BagAdapter
import com.sergeyrodin.matchesboxes.data.Bag

@BindingAdapter("bagsListData")
fun bindBagRecyclerView(recyclerView: RecyclerView, list: List<Bag>) {
    val adapter = recyclerView.adapter as BagAdapter
    adapter.submitList(list)
}

@BindingAdapter("noDataTextVisible")
fun bindNoDataViewVisibility(view: View, visible: Boolean) {
    view.visibility = if(visible) View.VISIBLE else View.GONE
}