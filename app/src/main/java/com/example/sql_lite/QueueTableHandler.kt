package com.example.sql_lite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class QueueTableHandler(var context: Context): SQLiteOpenHelper(context, db_name,null, 1) {
    companion object {
        private val db_name = "queue_database"
        private val tbl_name = "queued_songs"
        private val col_id = "id"
        private val col_title = "song_title"
        private val col_artist= "artist_name"
        private val col_album ="album_name"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val query =  "CREATE TABLE $tbl_name ($col_id INTEGER PRIMARY KEY, $col_title TEXT, $col_artist TEXT, $col_album TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL( "Drop Table if exist "+ tbl_name)
        onCreate(db)
    }
    fun create(mcont: StringArray){
        val db = this.writableDatabase
        val contVal = ContentValues()
        contVal.put(col_title,mcont.songName)
        contVal.put(col_album,mcont.albumName)
        contVal.put(col_artist, mcont.artistName)
        db.insert(tbl_name,null,contVal)
    }
    fun readAll() : MutableList<StringArray>{
        val db = this.readableDatabase
        val albumList = ArrayList<StringArray>()
        val query ="Select * from "+ tbl_name
        var cursor : Cursor? = null
        try{
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException){
            return albumList
        }
        var id : Int
        var title:String
        var artist :String
        var album : String
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(col_id))
                title = cursor.getString(cursor.getColumnIndex(col_title))
                artist = cursor.getString(cursor.getColumnIndex(col_artist))
                album = cursor.getString(cursor.getColumnIndex(col_album))
                var album = StringArray(id, title, artist, album)
                albumList.add(album)
            }while (cursor.moveToNext())
        }
        return  albumList
    }
    fun readOne(id : Int) : StringArray {
        val db = this.readableDatabase
        val albumList = StringArray(0, "", "","" )
        val query ="Select * from $tbl_name where id = $id"
        var cursor : Cursor? = null
        try{
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException){
            return albumList
        }
        var id : Int
        var title:String
        var artist :String
        var album : String
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndex(col_id))
            title = cursor.getString(cursor.getColumnIndex(col_title))
            artist = cursor.getString(cursor.getColumnIndex(col_artist))
            album = cursor.getString(cursor.getColumnIndex(col_album))
            val albumList = StringArray(id, title, artist, album)
        }
        return  albumList
    }
    fun delete(mcont: StringArray){
        val db = this.writableDatabase
        db.delete(tbl_name, "id= "+mcont.id,null)
    }
    fun update(mcont: StringArray) {
        val db = this.writableDatabase
        val contVal = ContentValues()
        contVal.put(col_title, mcont.songName)
        contVal.put(col_artist,mcont.artistName)
        contVal.put(col_artist,mcont.albumName)
        db.update(tbl_name, contVal, "id= "+mcont.id, null)
    }
}