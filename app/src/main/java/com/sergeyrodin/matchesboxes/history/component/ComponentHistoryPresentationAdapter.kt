package com.sergeyrodin.matchesboxes.history.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.databinding.DisplayComponentHistoryListItemBinding
import com.sergeyrodin.matchesboxes.history.HistoryPresentation
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationClickListener
import com.sergeyrodin.matchesboxes.history.all.HistoryPresentationDiffCallback

class DisplayComponentHistoryAdapter(
    private val longClickListener: HistoryPresentationClickListener,
    private val clickListener: ComponentHistoryPresentationClickListener
) : ListAdapter<HistoryPresentation, DisplayComponentHistoryAdapter.ViewHolder>(
    HistoryPresentationDiffCallback()
) {

    var highlightedItemId = -1
        set(value) {
            if(value != -1) {
                field = value
                val position = getPositionById(value)
                notifyItemChanged(position)
            }else {
                val position = getPositionById(field)
                field = value
                notifyItemChanged(position)
            }
        }

    private fun getPositionById(id: Int): Int {
        val item = currentList.find {
            it.id == id
        }
        return currentList.indexOf(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), highlightedItemId)
    }

    class ViewHolder private constructor(
        private val binding: DisplayComponentHistoryListItemBinding,
        private val clickListener: ComponentHistoryPresentationClickListener,
        private val longClickListener: HistoryPresentationClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            presentation: HistoryPresentation,
            highlightedItemId: Int
        ) {
            setupLongClickListener(presentation.id)
            setupClickListener()
            setupPresentation(presentation)

            if(highlightedItemId == presentation.id) {
                binding.layout.setBackgroundResource(R.color.secondaryLightColor)
            }else {
                binding.layout.setBackgroundResource(R.color.design_default_color_background)
            }

            executePendingBindings()
        }

        private fun setupLongClickListener(id: Int) {
            binding.layout.setOnLongClickListener {
                longClickListener.onClick(id)
                true
            }
        }

        private fun setupPresentation(presentation: HistoryPresentation) {
            binding.displayComponentHistory = presentation
        }

        private fun setupClickListener() {
            binding.clickListener = clickListener
        }

        private fun executePendingBindings() {
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                clickListener: ComponentHistoryPresentationClickListener,
                longClickListener: HistoryPresentationClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    DisplayComponentHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener, longClickListener)
            }
        }
    }
}

class ComponentHistoryPresentationClickListener(private val listener: () -> Unit) {
    fun onClick() = listener()
}