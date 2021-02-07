package com.sergeyrodin.matchesboxes.history.all

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.HistoryModel
import com.sergeyrodin.matchesboxes.databinding.DisplayHistoryListItemBinding

class HistoryModelAdapter(
    private val clickListener: HistoryModelClickListener,
    private val longClickListener: HistoryLongClickListener
) : ListAdapter<HistoryModel, HistoryModelAdapter.ViewHolder>(HistoryModelDiffCallback()) {

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

    class ViewHolder private constructor(private val binding: DisplayHistoryListItemBinding,
                                         private val modelClickListener: HistoryModelClickListener,
                                         private val longClickListener: HistoryLongClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: HistoryModel, highlightedItemId: Int) {
            setupClickListener(history.componentId, history.name)
            setupLongClickListener(history.id)
            setHistoryModel(history)

            if(highlightedItemId != history.id) {
                binding.historyItemLayout.setBackgroundResource(R.color.design_default_color_background)
            }else {
                binding.historyItemLayout.setBackgroundResource(R.color.secondaryLightColor)
            }

            binding.executePendingBindings()
        }

        private fun setupClickListener(componentId: Int, name: String) {
            binding.historyItemLayout.setOnClickListener {
                modelClickListener.onClick(componentId, name)
            }
        }

        private fun setupLongClickListener(id: Int) {
            binding.historyItemLayout.setOnLongClickListener {
                longClickListener.onClick(id)
                true
            }
        }

        private fun setHistoryModel(history: HistoryModel) {
            binding.historyModel = history
        }

        companion object {
            fun from(parent: ViewGroup,
                     clickListener: HistoryModelClickListener,
                     longClickListener: HistoryLongClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DisplayHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener, longClickListener)
            }
        }
    }
}

class HistoryModelClickListener(val clickListener: (componentId: Int, name: String) -> Unit) {
    fun onClick(componentId: Int, name: String) = clickListener(componentId, name)
}

class HistoryLongClickListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}

class HistoryModelDiffCallback : DiffUtil.ItemCallback<HistoryModel>() {
    override fun areItemsTheSame(
        oldItem: HistoryModel,
        newItem: HistoryModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: HistoryModel,
        newItem: HistoryModel
    ): Boolean {
        return oldItem == newItem
    }
}