package com.example.tasklist.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.R
import com.example.tasklist.data.Category
import com.example.tasklist.data.CategoryDAO

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val categoryDAO = CategoryDAO(this)

        val c1 = Category(-1, "Compra")
        val c2 = Category(-1, "Reforma")
        val c3 = Category(-1, "Viaje a Egipto")

        categoryDAO.insert(c1)
        categoryDAO.insert(c2)
        categoryDAO.insert(c3)

        categoryDAO.getAll().forEach {
            Log.i("TEST DB", it.toString())
        }

        val category1 = categoryDAO.getById(1)
        category1?.let {
            it.name = "Compra Mercadona"
            categoryDAO.update(it)
        }

        val category2 = categoryDAO.getById(2)
        category2?.let {
            categoryDAO.delete(it)
        }

        categoryDAO.getAll().forEach {
            Log.i("TEST DB", it.toString())
        }
    }
}