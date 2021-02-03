package com.sergeyrodin.matchesboxes.history.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.matchesboxes.R
import com.sergeyrodin.matchesboxes.data.History
import com.sergeyrodin.matchesboxes.databinding.DisplayComponentHistoryListItemBinding
import com.sergeyrodin.matchesboxes.history.all.HistoryLongClickListener

class HistoryAdapter(
    private val longClickListener: HistoryLongClickListener,
    private val clickListener: HistoryClickListener
) : ListAdapter<History, HistoryAdapter.ViewHolder>(
    HistoryDiffCallback()
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

    lateinit var deltas: Map<Int, String>

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
        val item = getItem(position)
        val delta = deltas[item.id] ?: ""
        holder.bind(item, delta, highlightedItemId)
    }

    class ViewHolder private constructor(
        private val binding: DisplayComponentHistoryListItemBinding,
        private val clickListener: HistoryClickListener,
        private val longClickListener: HistoryLongClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            history: History,
            delta: String,
            highlightedItemId: Int
        ) {
            setupLongClickListener(history.id)
            setupClickListener()
            setupHistory(history)

            if(highlightedItemId == history.id) {
                binding.layout.setBackgroundResource(R.color.secondaryLightColor)
            }else {
                binding.layout.setBackgroundResource(R.color.design_default_color_background)
            }

            binding.deltaText.text = delta

            executePendingBindings()
        }

        private fun setupLongClickListener(id: Int) {
            binding.layout.setOnLongClickListener {
                longClickListener.onClick(id)
                true
            }
        }

        private fun setupHistory(history: History) {
            binding.history = history
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
                clickListener: HistoryClickListener,
                longClickListener: HistoryLongClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    DisplayComponentHistoryListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener, longClickListener)
            }
        }
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<History>() {

    override fun areItemsTheSame(
        oldItem: History,
        newItem: History
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: History,
        newItem: History
    ): Boolean {
        return oldItem == newItem
    }

}

class HistoryClickListener(private val listener: () -> Unit) {
    fun onClick() = listener()
}