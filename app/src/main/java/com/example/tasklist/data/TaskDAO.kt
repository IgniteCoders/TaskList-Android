package com.example.tasklist.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.tasklist.utils.DatabaseManager

class TaskDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    fun save(task: Task) {
        if (task.id != -1) {
            update(task)
        } else {
            insert(task)
        }
    }

    fun getContentValues(task: Task): ContentValues {
        val values = ContentValues()
        values.put(Task.COLUMN_TITLE, task.title)
        values.put(Task.COLUMN_DONE, task.done)
        values.put(Task.COLUMN_CATEGORY_ID, task.category.id)
        return values
    }

    fun cursorToEntity(cursor: Cursor): Task {
        val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_TITLE))
        val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_DONE)) != 0
        val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_CATEGORY_ID))
        val category = CategoryDAO(context).getById(categoryId)!!
        return Task(itemId, title, done, category)
    }

    fun insert(task: Task) {
        // Gets the data repository in write mode
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insert(Task.TABLE_NAME, null, values)
            Log.i("DATABASE", "Inserted row with id $newRowId in table ${Task.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun update(task: Task) {
        // Gets the data repository in write mode
        open()

        // Create a new map of values, where column names are the keys
        val values = getContentValues(task)

        try {
            // Update the row, returning the count of affected rows
            val updatedRows = db.update(Task.TABLE_NAME, values, "${Task.COLUMN_ID} = ${task.id}", null)
            Log.i("DATABASE", "Updated $updatedRows rows with id ${task.id} in table ${Task.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun delete(task: Task) {
        open()

        try {
            // Issue SQL statement.
            val deletedRows = db.delete(Task.TABLE_NAME, "${Task.COLUMN_ID} = ${task.id}", null)
            Log.i("DATABASE", "Deleted $deletedRows rows with id ${task.id} from table ${Task.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

    }

    fun deleteAll() {
        open()

        try {
            // Issue SQL statement.
            val deletedRows = db.delete(Task.TABLE_NAME, null, null)
            Log.i("DATABASE", "Deleted $deletedRows rows from table ${Task.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

    }

    fun getById(id: Int) : Task? {
        open()

        var result: Task? = null

        try {
            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                "${Task.COLUMN_ID} = $id",              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            if (cursor.moveToNext()) {
                result = cursorToEntity(cursor)
            }

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        return result
    }

    fun getAllBy(where: String?) : List<Task> {
        open()

        val resultList: MutableList<Task> = mutableListOf()

        try {
            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                where,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                Task.COLUMN_DONE               // The sort order
            )

            while (cursor.moveToNext()) {
                val task = cursorToEntity(cursor)
                resultList.add(task)
            }

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        return resultList
    }

    fun getAll() : List<Task> {
        return getAllBy(null)
    }

    fun getAllByCategory(category: Category) : List<Task> {
        return getAllBy("${Task.COLUMN_CATEGORY_ID} = ${category.id}")
    }
}