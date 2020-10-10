package com.sergeyrodin.matchesboxes.popular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.databinding.PopularPresentationListItemBinding

class PopularPresentationAdapter :
    ListAdapter<PopularPresentation, PopularPresentationAdapter.ViewHolder>(
        PopularPresentationDiffCallback()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: PopularPresentationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(presentation: PopularPresentation) {
            binding.presentation = presentation
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    PopularPresentationListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class PopularPresentationDiffCallback : DiffUtil.ItemCallback<PopularPresentation>() {
    override fun areItemsTheSame(
        oldItem: PopularPresentation,
        newItem: PopularPresentation
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: PopularPresentation,
        newItem: PopularPresentation
    ): Boolean {
        return oldItem == newItem
    }

}