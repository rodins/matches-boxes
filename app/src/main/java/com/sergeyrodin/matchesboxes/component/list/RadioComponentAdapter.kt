package com.sergeyrodin.matchesboxes.component.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.data.RadioComponent
import com.sergeyrodin.matchesboxes.databinding.RadioComponentListItemBinding

class RadioComponentAdapter(private val clickListener: RadioComponentListener)
    : ListAdapter<RadioComponent, RadioComponentAdapter.ViewHolder>(RadioComponentDiffCallback()){

    class ViewHolder private constructor(private val binding: RadioComponentListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(component: RadioComponent, listener: RadioComponentListener) {
            binding.radioComponent = component
            binding.clickListener = listener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RadioComponentListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}

class RadioComponentListener(private val clickListener: (componentId: Int) -> Unit) {
    fun onClick(componentId: Int) = clickListener(componentId)
}

class RadioComponentDiffCallback: DiffUtil.ItemCallback<RadioComponent>() {
    override fun areItemsTheSame(oldItem: RadioComponent, newItem: RadioComponent): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RadioComponent, newItem: RadioComponent): Boolean {
        return oldItem == newItem
    }

}