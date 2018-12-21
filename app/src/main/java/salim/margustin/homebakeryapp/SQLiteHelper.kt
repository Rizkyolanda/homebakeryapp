package salim.margustin.homebakeryapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {

    fun queryData(sql: String) {
        val database = writableDatabase
        database.execSQL(sql)
    }

    fun insertData(name: String, ingredient: String, recipes: String, image: ByteArray) {
        val database = writableDatabase
        val sql = "INSERT INTO ITEM VALUES (NULL,? ,?, ?, ?)"

        val statement = database.compileStatement(sql)
        statement.clearBindings()

        statement.bindString(1, name)
        statement.bindString(2, ingredient)
        statement.bindString(3, recipes)
        statement.bindBlob(4, image)

        statement.executeInsert()
    }

    fun updateData(name: String, ingredient: String, recipes: String, image: ByteArray, id: Int) {
        val database = writableDatabase

        val sql = "UPDATE ITEM SET name = ?, ingredient = ?, recipes = ?, image = ? WHERE id = ?"
        val statement = database.compileStatement(sql)

        statement.bindString(1, name)
        statement.bindString(2, ingredient)
        statement.bindString(3, recipes)
        statement.bindBlob(4, image)
        statement.bindDouble(5, id.toDouble())

        statement.execute()
        database.close()
    }

    fun deleteData(id: Int) {
        val database = writableDatabase

        val sql = "DELETE FROM ITEM WHERE id = ?"
        val statement = database.compileStatement(sql)
        statement.clearBindings()
        statement.bindDouble(1, id.toDouble())

        statement.execute()
        database.close()
    }

    fun getData(sql: String): Cursor {
        val database = readableDatabase
        return database.rawQuery(sql, null)
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }
}