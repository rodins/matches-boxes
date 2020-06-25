package com.sergeyrodin.matchesboxes.bag.list


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.databinding.BagListItemBinding
import com.sergeyrodin.matchesboxes.util.DisplayQuantity

class BagAdapter(private val bagListener: BagListener) : ListAdapter<DisplayQuantity, BagAdapter.ViewHolder>(BagDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BagAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position), bagListener)
    }

    class ViewHolder private constructor(private val binding: BagListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(displayQuantity: DisplayQuantity, bagListener: BagListener) {
            binding.displayQuantity = displayQuantity
            binding.clickListener = bagListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BagListItemBinding.inflate(layoutInflater, parent, false)
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

class BagListener(val clickListener: (bagId: Int) -> Unit) {
    fun onClick(bagId: Int) = clickListener(bagId)
}