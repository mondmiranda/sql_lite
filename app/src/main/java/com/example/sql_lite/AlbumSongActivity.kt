package com.example.sql_lite

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.sqlite.adapter.listAdapter

class AlbumSongActivity : AppCompatActivity() {
    var arrayList = mutableListOf<stringArray>()
    lateinit var songTitle : String
    lateinit var artistName : String
    lateinit var albumTitle : String
    lateinit var adapter: listAdapter
    lateinit var titleDisplay: TextView
    val songAlbumdb = albumSongHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_album)
        val image = findViewById<ImageView>(R.id.imgCountry)
        arrayList = songAlbumdb.readAll()
        titleDisplay = findViewById(R.id.albumTitleDisp)
        titleDisplay.text = songAlbumdb.getTblName()
        val listview = findViewById<ListView>(R.id.countrList)
        adapter = listAdapter(this, R.layout.main_row, arrayList)
        listview.adapter = adapter
        registerForContextMenu(listview)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_song, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_song -> {
                addDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    fun addDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.main_add_song, null)
        dialogBuilder.setView(view)
                .setTitle("Add Song")
                .setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialogInterface, i ->

                })
                .setPositiveButton("Add Song", DialogInterface.OnClickListener{
                    dialogInterface, i ->
                    val inputTitle = view.findViewById<EditText>(R.id.songTitle)
                    val inputArtist = view.findViewById<EditText>(R.id.artistName)
                    val inputAlbum = view.findViewById<EditText>(R.id.albumName)
                    songTitle = inputTitle.text.toString()
                    artistName = inputArtist.text.toString()
                    albumTitle = inputArtist.text.toString()
                    if (albumTitle.isEmpty() == true){
                        albumTitle = "Unknown"
                    }
                    else if (artistName.isEmpty() == true){
                        artistName = "Unknown"
                    }
                    else if (albumTitle.isEmpty() == true){
                        albumTitle = "Unknown"
                    }
                    val albumObject= stringArray(songName = albumTitle,artistName = artistName, albumName = albumTitle)
                    songAlbumdb.create(albumObject)
                })
        adapter.notifyDataSetChanged()
        return dialogBuilder.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val albumPosition = info.position
        val album_id = arrayList[albumPosition].id
        songAlbumdb.readOne(album_id)

        return when(item.itemId){
            R.id.remove ->{
                songAlbumdb.delete(album_id)
                true
            }
            else ->return super.onContextItemSelected(item)
        }
    }
}