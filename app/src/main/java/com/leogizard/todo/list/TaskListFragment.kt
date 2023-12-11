package com.leogizard.todo.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leogizard.todo.R
import com.leogizard.todo.databinding.FragmentTaskListBinding
import com.leogizard.todo.detail.DetailActivity
import java.util.UUID


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding ?= null
    private val binding get() = _binding!!

    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val task = result.data?.getSerializableExtra("task") as Task?
        taskList = taskList + task!!
        adapter.submitList(taskList)
    }

    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val task = result.data?.getSerializableExtra("task") as Task
        taskList = taskList.map { if (it.id == task.id) task else it }
        adapter.submitList(taskList)
    }

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    val adapterListener: TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskList = taskList - task
            adapter.submitList(taskList)
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("task",task)
            editTask.launch(intent)
        }
    }
    private val adapter = TaskListAdapter(adapterListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container,false)
        val view = binding.root
        adapter.submitList(taskList)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.tasklistRecyclerview
        val addButton = binding.floatingActionButton
        addButton.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }

        recyclerView.adapter = adapter
    }
}