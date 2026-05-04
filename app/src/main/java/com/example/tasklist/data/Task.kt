package com.example.tasklist.data

data class Task (
    val id: Int,
    var title: String,
    var done: Boolean,
    val category: Category
){
    companion object {
        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DONE = "done"
        const val COLUMN_CATEGORY_ID = "category_id"

        const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_TITLE TEXT," +
                    "$COLUMN_DONE BOOLEAN," +
                    "$COLUMN_CATEGORY_ID INTEGER," +
                    "CONSTRAINT fk_category FOREIGN KEY($COLUMN_CATEGORY_ID) " +
                    "REFERENCES ${Category.TABLE_NAME}(${Category.COLUMN_ID}) ON DELETE CASCADE)"

        const val SQL_DELETE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}