package com.example.tasklist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.tasklist.utils.DatabaseManager

class CategoryDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    fun insert(category: Category) {
        // Gets the data repository in write mode
        open()

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(Category.COLUMN_NAME, category.name)

        try {
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insert(Category.TABLE_NAME, null, values)
            Log.i("DATABASE", "Inserted row with id $newRowId in table ${Category.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun update(category: Category) {
        // Gets the data repository in write mode
        open()

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(Category.COLUMN_NAME, category.name)

        try {
            // Update the row, returning the count of affected rows
            val updatedRows = db.update(Category.TABLE_NAME, values, "${Category.COLUMN_ID} = ${category.id}", null)
            Log.i("DATABASE", "Updated $updatedRows rows with id ${category.id} in table ${Category.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun delete(category: Category) {
        open()

        try {
            // Issue SQL statement.
            val deletedRows = db.delete(Category.TABLE_NAME, "${Category.COLUMN_ID} = ${category.id}", null)
            Log.i("DATABASE", "Deleted $deletedRows rows with id ${category.id} in table ${Category.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

    }

    fun getById(id: Int) : Category? {
        open()

        var result: Category? = null

        try {
            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                "${Category.COLUMN_ID} = $id",              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            if (cursor.moveToNext()) {
                val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME))
                result = Category(itemId, title)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        return result
    }

    fun getAll() : List<Category> {
        open()

        val resultList: MutableList<Category> = mutableListOf()

        try {
            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            while (cursor.moveToNext()) {
                val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Category.COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME))
                val category = Category(itemId, title)
                resultList.add(category)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        return resultList
    }
}