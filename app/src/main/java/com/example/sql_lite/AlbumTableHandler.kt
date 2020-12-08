package com.example.sql_lite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class AlbumTableHandler(var context: Context): SQLiteOpenHelper(context, db_name,null, db_ver) {
    companion object {
        private val db_ver = 1
        private val db_name = "album_database"
        private val tbl_name = "albums"
        private val col_id = "id"
        private val col_title = "album_title"
        private val col_date= "release_date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =  "CREATE TABLE $tbl_name ($col_id INTEGER PRIMARY KEY, $col_title TEXT, $col_date TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL( "Drop Table if exist "+ tbl_name)
        onCreate(db)
    }
    fun create(mcont : albumArr){
        val db = this.writableDatabase
        val contVal = ContentValues()
        contVal.put(col_title, mcont.title)
        contVal.put(col_date,mcont.date)
        db.insert(tbl_name,null, contVal)
    }
    fun readAll() : MutableList<albumArr>{
        val albumList = ArrayList<albumArr>()
        val query ="Select * from "+ tbl_name
        var cursor : Cursor? = null
        val db = this.readableDatabase
        try{
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException){
            return albumList
        }
        var id : Int
        var title:String
        var dateRel :String
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(col_id))
                title = cursor.getString(cursor.getColumnIndex(col_title))
                dateRel = cursor.getString(cursor.getColumnIndex(col_date))
                val album = albumArr(id,title, dateRel)
                albumList.add(album)
            }while (cursor.moveToNext())
        }
        return  albumList
    }
    fun readOne(id : Int) : albumArr{
        val db = this.readableDatabase
        val albumList = albumArr(0, "", "")
        val query ="Select * from $tbl_name where id = $id"
        var cursor : Cursor? = null
        try{
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException){
            return albumList
        }
        var id : Int
        var title:String
        var dateRel :String
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndex(col_id))
            title = cursor.getString(cursor.getColumnIndex(col_title))
            dateRel = cursor.getString(cursor.getColumnIndex(col_date))
            val album = albumArr(id,title, dateRel)
        }
        return  albumList
    }

    fun update(mcont: albumArr) {
        val db = this.writableDatabase
        val contVal = ContentValues()
        contVal.put(col_title, mcont.title)
        contVal.put(col_date,mcont.date)
        db.update(tbl_name, contVal, "id= "+mcont.id, null)
    }
    fun delete(mcont: albumArr){
        val db = this.writableDatabase
        db.delete(tbl_name, "id= "+mcont.id,null)
    }

}