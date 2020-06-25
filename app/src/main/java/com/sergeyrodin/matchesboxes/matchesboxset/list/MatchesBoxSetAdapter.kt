package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.MatchesBoxSetListItemBinding
import com.sergeyrodin.matchesboxes.util.DisplayQuantity

class MatchesBoxSetAdapter(private val clickListener: MatchesBoxSetListener)
    : ListAdapter<DisplayQuantity, MatchesBoxSetAdapter.ViewHolder>(MatchesBoxSetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MatchesBoxSetListItemBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(matchesBoxSet: DisplayQuantity, matchesBoxSetListener: MatchesBoxSetListener) {
            binding.matchesBoxSet = matchesBoxSet
            binding.clickListener = matchesBoxSetListener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MatchesBoxSetListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class MatchesBoxSetDiffCallback: DiffUtil.ItemCallback<DisplayQuantity>() {
    override fun areItemsTheSame(oldItem: DisplayQuantity, newItem: DisplayQuantity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DisplayQuantity, newItem: DisplayQuantity): Boolean {
        return oldItem == newItem
    }
}

class MatchesBoxSetListener(val clickListener: (matchesBoxSetId: Int) -> Unit) {
    fun onClick(matchesBoxSetId: Int) = clickListener(matchesBoxSetId)
}