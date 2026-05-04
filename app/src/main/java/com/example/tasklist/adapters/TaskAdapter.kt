package com.example.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.adapters.utils.TaskDiffUtils
import com.example.tasklist.data.Task
import com.example.tasklist.databinding.ItemTaskBinding

class TaskAdapter(
    var items: List<Task>,
    val onClick: (Int) -> Unit,
    val onEdit: (Int) -> Unit,
    val onDelete: (Int) -> Unit,
) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.render(task)
        holder.itemView.setOnClickListener {
            onClick(holder.absoluteAdapterPosition)
        }
        holder.binding.doneCheckBox.setOnCheckedChangeListener { _, _ ->
            if (holder.binding.doneCheckBox.isPressed) {
                onClick(holder.absoluteAdapterPosition)
            }
        }
        holder.binding.editButton.setOnClickListener {
            onEdit(holder.absoluteAdapterPosition)
        }
        holder.binding.deleteButton.setOnClickListener {
            onDelete(holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataSet: List<Task>) {
        val diffUtils = TaskDiffUtils(items, dataSet)
        val diffResult = DiffUtil.calculateDiff(diffUtils)
        items = dataSet
        diffResult.dispatchUpdatesTo(this)
    }
}

class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(task: Task) {
        binding.doneCheckBox.isChecked = task.done
        binding.titleTextView.text = task.title
    }

}