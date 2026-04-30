package com.example.tasklist.data

data class Category(
    val id: Int,
    var name: String
) {
    companion object {
        const val TABLE_NAME = "categories"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"

        const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_NAME TEXT)"

        const val SQL_DELETE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
