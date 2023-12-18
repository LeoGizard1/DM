package com.leogizard.todo.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.leogizard.todo.R
import com.leogizard.todo.data.Api
import com.leogizard.todo.data.TaskListViewModel
import com.leogizard.todo.databinding.FragmentTaskListBinding
import com.leogizard.todo.detail.DetailActivity
import com.leogizard.todo.user.UserActivity
import kotlinx.coroutines.launch


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val task = result.data?.getSerializableExtra("task") as Task
        viewModel.add(task)
    }

    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val task = result.data?.getSerializableExtra("task") as Task
        viewModel.update(task)
    }



    val adapterListener: TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.remove(task)
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.tasklistRecyclerview
        val addButton = binding.floatingActionButton
        addButton.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }
        binding.userPic.setOnClickListener{
            val intent = Intent(context, UserActivity::class.java)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est exécutée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val user = Api.userWebService.fetchUser().body()!!
            binding.user.text = user.name
            binding.userPic.load(user.avatar) {
                error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
            }
        }
        viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
    }
}