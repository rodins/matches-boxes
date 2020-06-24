package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.databinding.MatchesBoxSetListItemBinding
import com.sergeyrodin.matchesboxes.util.MatchesBoxSetQuantity

class MatchesBoxSetAdapter(private val clickListener: MatchesBoxSetListener)
    : ListAdapter<MatchesBoxSetQuantity, MatchesBoxSetAdapter.ViewHolder>(MatchesBoxSetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MatchesBoxSetListItemBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(matchesBoxSet: MatchesBoxSetQuantity, matchesBoxSetListener: MatchesBoxSetListener) {
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

class MatchesBoxSetDiffCallback: DiffUtil.ItemCallback<MatchesBoxSetQuantity>() {
    override fun areItemsTheSame(oldItem: MatchesBoxSetQuantity, newItem: MatchesBoxSetQuantity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MatchesBoxSetQuantity, newItem: MatchesBoxSetQuantity): Boolean {
        return oldItem == newItem
    }
}

class MatchesBoxSetListener(val clickListener: (matchesBoxSetId: Int) -> Unit) {
    fun onClick(matchesBoxSetId: Int) = clickListener(matchesBoxSetId)
}