package com.example.tasklist.activities

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.adapters.TaskAdapter
import com.example.tasklist.data.Category
import com.example.tasklist.data.CategoryDAO
import com.example.tasklist.data.Task
import com.example.tasklist.data.TaskDAO
import com.example.tasklist.databinding.ActivityTaskListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.Locale

class TaskListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
    }

    lateinit var binding: ActivityTaskListBinding

    lateinit var categoryDAO: CategoryDAO
    lateinit var taskDAO: TaskDAO

    var category: Category? = null
    var taskList: List<Task> = emptyList()

    lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        categoryDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1)
        category = categoryDAO.getById(categoryId)

        supportActionBar?.title = category?.name

        adapter = TaskAdapter(taskList, ::showTask, ::editTask, ::deleteTask)
        binding.recyclerView.adapter = adapter

        binding.addTaskFAB.setOnClickListener {
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra(TaskDetailActivity.EXTRA_CATEGORY_ID, category?.id ?: -1)
            startActivity(intent)
        }

        configureGestures()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
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

    fun reloadData() {
        category?.let {
            taskList = taskDAO.getAllByCategory(it)
        }
        adapter.updateData(taskList)
    }

    fun showTask(position: Int) {
        val task = taskList[position]

        task.done = !task.done
        taskDAO.update(task)

        adapter.notifyItemChanged(position)
        reloadData()
    }

    fun editTask(position: Int) {
        val task = taskList[position]

        val intent = Intent(this, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_CATEGORY_ID, task.category.id)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
        startActivity(intent)

        adapter.notifyItemChanged(position)
    }

    fun deleteTask(position: Int) {
        val task = taskList[position]

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Borrar tarea")
            .setMessage("¿Está usted seguro de querer borrar la tarea \"${task.title}\"?")
            .setPositiveButton("Si") { dialog, which ->
                taskDAO.delete(task)
                reloadData()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                adapter.notifyItemChanged(position)
            }
            //.setCancelable(false)
            .create()

        dialog.show()
    }

    fun configureGestures() {
        val gestures = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    adapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if (direction == ItemTouchHelper.LEFT) {
                        deleteTask(viewHolder.absoluteAdapterPosition)
                    } else {
                        editTask(viewHolder.absoluteAdapterPosition)
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                    val whiteColor = getColor(R.color.white)

                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        // Swipe left action
                        .addSwipeLeftLabel("BORRAR")
                        .setSwipeLeftLabelColor(whiteColor)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftActionIconTint(whiteColor)
                        .addSwipeLeftBackgroundColor(getColor(R.color.negative))

                        // Swipe right action
                        .addSwipeRightLabel("EDITAR")
                        .setSwipeRightLabelColor(whiteColor)
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .setSwipeRightActionIconTint(whiteColor)
                        .addSwipeRightBackgroundColor(getColor(R.color.black))

                        // Build
                        .create()
                        .decorate()

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

            }
        )
        gestures.attachToRecyclerView(binding.recyclerView)
    }
}