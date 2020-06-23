package com.sergeyrodin.matchesboxes.matchesbox.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.data.MatchesBox
import com.sergeyrodin.matchesboxes.databinding.MatchesBoxListItemBinding
import com.sergeyrodin.matchesboxes.util.MatchesBoxQuantity

class MatchesBoxAdapter(private val clickListener: MatchesBoxListener):
    ListAdapter<MatchesBoxQuantity, MatchesBoxAdapter.ViewHolder>(MatchesBoxDiffCallback()) {

    class ViewHolder private constructor(private val binding: MatchesBoxListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(matchesBox: MatchesBoxQuantity, listener: MatchesBoxListener) {
            binding.matchesBox = matchesBox
            binding.clickListener = listener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MatchesBoxListItemBinding.inflate(layoutInflater, parent, false)
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

class MatchesBoxDiffCallback: DiffUtil.ItemCallback<MatchesBoxQuantity>() {
    override fun areItemsTheSame(oldItem: MatchesBoxQuantity, newItem: MatchesBoxQuantity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MatchesBoxQuantity, newItem: MatchesBoxQuantity): Boolean {
        return oldItem == newItem
    }
}

class MatchesBoxListener(val clickListener: (boxId: Int) -> Unit) {
    fun onClick(boxId: Int) = clickListener(boxId)
}