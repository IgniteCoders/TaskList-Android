package com.example.tasklist.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.R
import com.example.tasklist.data.Category
import com.example.tasklist.data.CategoryDAO
import com.example.tasklist.data.Task
import com.example.tasklist.data.TaskDAO
import com.example.tasklist.databinding.ActivityTaskDetailBinding
import com.google.android.material.snackbar.Snackbar

class TaskDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
        const val EXTRA_TASK_ID     = "TASK_ID"
    }

    lateinit var binding: ActivityTaskDetailBinding

    lateinit var categoryDAO: CategoryDAO
    lateinit var taskDAO: TaskDAO

    lateinit var task: Task
    var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1)

        category = categoryDAO.getById(categoryId)
        if (taskId != -1) {
            task = taskDAO.getById(taskId)!!
            supportActionBar?.title = "Editar tarea"
        } else {
            task = Task(-1, "", false, category!!)
            supportActionBar?.title = "Crear tarea"
        }


        binding.titleTextField.editText!!.setText(task.title)

        binding.saveButton.setOnClickListener {
            task.title = binding.titleTextField.editText!!.text.toString()
            taskDAO.save(task)
            //Snackbar.make(binding.root, "Tarea guardada correctamente", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}