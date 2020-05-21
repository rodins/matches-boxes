package com.sergeyrodin.matchesboxes.bag.list


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.data.Bag
import com.sergeyrodin.matchesboxes.databinding.BagListItemBinding

class BagAdapter(private val bagListener: BagListener) : ListAdapter<Bag, BagAdapter.ViewHolder>(BagDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BagAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position), bagListener)
    }

    class ViewHolder private constructor(private val binding: BagListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(bag: Bag, bagListener: BagListener) {
            binding.bag = bag
            binding.clickListener = bagListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BagListItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }
    }
}

class BagDiffCallback: DiffUtil.ItemCallback<Bag>() {
    override fun areItemsTheSame(oldItem: Bag, newItem: Bag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Bag, newItem: Bag): Boolean {
        return oldItem == newItem
    }
}

class BagListener(val clickListener: (bagId: Int) -> Unit) {
    fun onClick(bagId: Int) = clickListener(bagId)
}