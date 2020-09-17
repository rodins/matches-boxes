package com.sergeyrodin.matchesboxes.bag.list


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.DisplayQuantityListItemBinding
import com.sergeyrodin.matchesboxes.data.ItemWithQuantityPresentation

class DisplayQuantityAdapter(
    private val displayQuantityListener: DisplayQuantityListener,
    private val itemIconResource: Int) : ListAdapter<ItemWithQuantityPresentation, DisplayQuantityAdapter.ViewHolder>(BagDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), displayQuantityListener, itemIconResource)
    }

    class ViewHolder private constructor(private val binding: DisplayQuantityListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(itemWithQuantityPresentation: ItemWithQuantityPresentation,
                 displayQuantityListener: DisplayQuantityListener,
                 itemIconResource: Int) {
            setItemIcon(itemIconResource)
            setItemDataAndListener(itemWithQuantityPresentation, displayQuantityListener)
        }

        private fun setItemIcon(itemIconResource: Int) {
            binding.itemIcon.tag = itemIconResource
            binding.itemIcon.setImageResource(itemIconResource)
        }

        private fun setItemDataAndListener(
            itemWithQuantityPresentation: ItemWithQuantityPresentation,
            displayQuantityListener: DisplayQuantityListener
        ) {
            binding.displayQuantity = itemWithQuantityPresentation
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

class BagDiffCallback: DiffUtil.ItemCallback<ItemWithQuantityPresentation>() {
    override fun areItemsTheSame(oldItem: ItemWithQuantityPresentation, newItem: ItemWithQuantityPresentation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ItemWithQuantityPresentation, newItem: ItemWithQuantityPresentation): Boolean {
        return oldItem == newItem
    }
}

class DisplayQuantityListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}