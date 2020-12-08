package com.example.sql_lite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class albumSongHandler(var context: Context): SQLiteOpenHelper(context, db_name, null, db_ver) {
    companion object{
        private val db_ver = 1
        private val db_name = "album_song_databases"
        private val col_id = "id"
        private lateinit var tble_name: String
        private val col_title = "song_title"
        private val col_artist= "artist_name"
        private val col_album ="album_name"
    }
    fun tbleName(mTable: String): String{
        tble_name = mTable
        return  tble_name
    }
    fun getTblName(): String{
        return tble_name
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val query =  "CREATE TABLE $tble_name ($col_id INTEGER PRIMARY KEY,$col_title TEXT, $col_artist TEXT, $col_album TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL( "Drop Table if exist "+ tble_name)
        onCreate(db)
    }
    fun readAll() : MutableList<stringArray>{
        val db = this.readableDatabase
        val albumList = ArrayList<stringArray>()
        val query ="Select * from "+ tble_name
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
                var album = stringArray(id, title, artist, album)
                albumList.add(album)
            }while (cursor.moveToNext())
        }
        return  albumList
    }

    fun create(mcont: stringArray){
        val db = this.writableDatabase
        val contVal = ContentValues()
        contVal.put(col_title,mcont.songName)
        contVal.put(col_album,mcont.albumName)
        contVal.put(col_artist, mcont.artistName)
        db.insert(tble_name,null,contVal)
    }

    fun delete(id: Int){
        val db = this.writableDatabase
        db.delete(tble_name, "id= "+ id,null)
    }
    fun readOne(id : Int) : stringArray{
        val db = this.readableDatabase
        val albumList = stringArray(0, "", "","" )
        val query ="Select * from $tble_name where id = $id"
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
            val albumList = stringArray(id, title, artist, album)
        }
        return  albumList
    }
}