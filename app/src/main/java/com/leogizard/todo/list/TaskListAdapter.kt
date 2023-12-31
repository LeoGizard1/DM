package com.leogizard.todo.list

import android.accessibilityservice.AccessibilityService.TakeScreenshotCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leogizard.todo.R
import com.leogizard.todo.databinding.FragmentTaskListBinding

object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem == newItem
    }
}

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}

class TaskListAdapter(val listener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback){

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      fun bind(task: Task){
          itemView.findViewById<TextView>(R.id.task_title).text = task.title
          itemView.findViewById<TextView>(R.id.task_description).text = task.description
          itemView.findViewById<ImageButton>(R.id.delete_task_button).setOnClickListener {listener.onClickDelete(task)}
          itemView.findViewById<ImageButton>(R.id.edit_task_button).setOnClickListener {listener.onClickEdit(task)}
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent, false)
        return TaskViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}