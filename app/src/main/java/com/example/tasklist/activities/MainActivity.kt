package com.example.tasklist.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.R
import com.example.tasklist.adapters.CategoryAdapter
import com.example.tasklist.data.Category
import com.example.tasklist.data.CategoryDAO
import com.example.tasklist.databinding.ActivityMainBinding
import com.example.tasklist.databinding.DialogCreateCategoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var adapter: CategoryAdapter

    var categoryList: List<Category> = emptyList()

    lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryDAO = CategoryDAO(this)

        /*for (i in 1..10) {
            val category = Category(-1, "Category $i")
            categoryDAO.insert(category)
        }*/

        categoryList = categoryDAO.getAll()

        adapter = CategoryAdapter(categoryList, ::showCategory, ::editCategory, ::deleteCategory)

        binding.recyclerView.adapter = adapter

        binding.addCategoryFAB.setOnClickListener {
            showCategoryDialog(Category(-1, ""))
        }
    }

    fun showCategoryDialog(category: Category) {
        val dialogBinding = DialogCreateCategoryBinding.inflate(layoutInflater)

        val isEditing = category.id != -1

        var title = "Crear categoría"
        if (isEditing) {
            title = "Editar categoría"
        }

        dialogBinding.textField.editText!!.setText(category.name)

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_add_category)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { dialog, which ->
                val name = dialogBinding.textField.editText!!.text.toString()
                category.name = name
                categoryDAO.save(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            //.setCancelable(false)
            .create()

        dialog.show()
    }

    fun showCategory(position: Int) {
        val category = categoryList[position]
        Toast.makeText(this, category.name, Toast.LENGTH_SHORT).show()
    }

    fun editCategory(position: Int) {
        val category = categoryList[position]
        showCategoryDialog(category)
    }

    fun deleteCategory(position: Int) {
        val category = categoryList[position]

        val dialog = MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_delete)
            .setTitle("Borrar categoría")
            .setMessage("¿Está usted seguro de querer borrar la categoría \"${category.name}\"?")
            .setPositiveButton("Si") { dialog, which ->
                categoryDAO.delete(category)
                categoryList = categoryDAO.getAll()
                adapter.updateData(categoryList)
            }
            .setNegativeButton("Cancelar") { dialog, which ->

            }
            //.setCancelable(false)
            .create()

        dialog.show()

    }
}