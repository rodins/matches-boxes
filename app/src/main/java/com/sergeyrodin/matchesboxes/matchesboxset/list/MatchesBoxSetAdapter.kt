package com.sergeyrodin.matchesboxes.matchesboxset.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.data.MatchesBoxSet
import com.sergeyrodin.matchesboxes.databinding.MatchesBoxSetListItemBinding

class MatchesBoxSetAdapter(private val clickListener: MatchesBoxSetListener)
    : ListAdapter<MatchesBoxSet, MatchesBoxSetAdapter.ViewHolder>(MatchesBoxSetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MatchesBoxSetListItemBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind(matchesBoxSet: MatchesBoxSet, matchesBoxSetListener: MatchesBoxSetListener) {
            binding.matchesBoxSet = matchesBoxSet
            binding.clickListener = matchesBoxSetListener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MatchesBoxSetListItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }
    }
}

class MatchesBoxSetDiffCallback: DiffUtil.ItemCallback<MatchesBoxSet>() {
    override fun areItemsTheSame(oldItem: MatchesBoxSet, newItem: MatchesBoxSet): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MatchesBoxSet, newItem: MatchesBoxSet): Boolean {
        return oldItem == newItem
    }
}

class MatchesBoxSetListener(val clickListener: (matchesBoxSetId: Int) -> Unit) {
    fun onClick(matchesBoxSetId: Int) = clickListener(matchesBoxSetId)
}