package com.sergeyrodin.matchesboxes.bag.list


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.DisplayQuantityListItemBinding
import com.sergeyrodin.matchesboxes.util.DisplayQuantity

class DisplayQuantityAdapter(private val displayQuantityListener: DisplayQuantityListener) : ListAdapter<DisplayQuantity, DisplayQuantityAdapter.ViewHolder>(BagDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), displayQuantityListener)
    }

    class ViewHolder private constructor(private val binding: DisplayQuantityListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(displayQuantity: DisplayQuantity, displayQuantityListener: DisplayQuantityListener) {
            binding.displayQuantity = displayQuantity
            binding.clickListener = displayQuantityListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayQuantityListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class BagDiffCallback: DiffUtil.ItemCallback<DisplayQuantity>() {
    override fun areItemsTheSame(oldItem: DisplayQuantity, newItem: DisplayQuantity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DisplayQuantity, newItem: DisplayQuantity): Boolean {
        return oldItem == newItem
    }
}

class DisplayQuantityListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}