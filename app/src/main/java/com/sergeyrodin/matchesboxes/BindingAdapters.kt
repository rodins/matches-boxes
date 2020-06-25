package com.sergeyrodin.matchesboxes

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.bag.list.DisplayQuantityAdapter
import com.sergeyrodin.matchesboxes.component.list.RadioComponentAdapter
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.util.DisplayQuantity

@BindingAdapter("displayQuantityListData")
fun bindBagRecyclerView(recyclerView: RecyclerView, list: List<DisplayQuantity>?) {
    val adapter = recyclerView.adapter as DisplayQuantityAdapter
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