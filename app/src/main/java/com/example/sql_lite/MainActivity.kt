package com.example.sqlite

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.sqlite.adapter.listAdapter
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    var queuedList = arrayListOf<StringArray>()
    var list = mutableListOf<StringArray>()
    val dbHandler = SongTableHandler(this)
    lateinit var songTitle : String
    lateinit var artistName : String
    lateinit var albumTitle : String
    lateinit var adapter: listAdapter
    lateinit var songListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("SQLite")
        list = dbHandler.readAll()
        songListView = findViewById<ListView>(R.id.song_list)
        adapter = listAdapter(this,R.layout.main_row, list)
        songListView.adapter =  adapter
        registerForContextMenu(songListView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.queue -> {
                val intent1 = Intent(this, QueuedSongsActivity::class.java)
                startActivity(intent1)
                true
            }
            R.id.songs -> {
                true
            }
            R.id.albums -> {
                startActivity(Intent(this, albumsActivity::class.java))
                true
            }
            R.id.add_song->{
                addDialog()
                true
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
                    val albumObject= StringArray(songName = albumTitle,artistName = artistName, albumName = albumTitle)
                    dbHandler.create(albumObject)
                })
        adapter.notifyDataSetChanged()
        return dialogBuilder.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.queue_menu,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val songPosition = info.position
        val songId = list[songPosition].id
        dbHandler.readOne(songId)
        songTitle = list[songPosition].songName
        artistName = list[songPosition].artistName
        albumTitle = list[songPosition].albumName
        val song = StringArray(songId,songName = songTitle,artistName = artistName,albumName = albumTitle)
        return when(item.itemId){
            R.id.add_queue -> {
                val dbqueueHandler = QueueTableHandler(this)
                dbqueueHandler.create(song)
                val  snack = Snackbar.make(findViewById(R.id.coordinatorLayoutroot), "Add to QUEUE", Snackbar.LENGTH_LONG)
                        .setAction("View",View.OnClickListener {
                            val intent1 = Intent(this, QueuedSongsActivity::class.java)
                            startActivity(intent1)
                        })
                snack.show()
                true
            }
            R.id.delete ->{
                val dialogBuilder = AlertDialog.Builder(this)
                        .setTitle("Delete? $songTitle")
                        .setNegativeButton("No", DialogInterface.OnClickListener{
                            dialogInterface, i ->

                        })
                        .setPositiveButton("Yes", DialogInterface.OnClickListener{
                            dialogInterface, i ->
                            val albumObject= StringArray(songId, songTitle, artistName,albumTitle)
                            dbHandler.delete(albumObject)
                        })
                dialogBuilder.show()
                true
            }
            R.id.edit->{
                val inflater = this.layoutInflater
                val view = inflater.inflate(R.layout.main_edit_text, null)
                val inputTitle = view.findViewById<EditText>(R.id.songTitle)
                val inputArtist = view.findViewById<EditText>(R.id.artistName)
                val inputAlbum = view.findViewById<EditText>(R.id.albumName)
                inputTitle.setText(songTitle)
                inputArtist.setText(artistName)
                inputAlbum.setText(albumTitle)
                val dialogBuilder = AlertDialog.Builder(this)
                        .setTitle("Edit Song")
                        .setView(view)
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener{
                            dialogInterface,i ->
                        })
                        .setPositiveButton("Edit", DialogInterface.OnClickListener {
                            dialogInterface, i ->
                            songTitle = inputTitle.text.toString()
                            albumTitle = inputAlbum.text.toString()
                            artistName = inputArtist.text.toString()
                            if (albumTitle.isEmpty() == true){
                                albumTitle = "Unknown"
                            }
                            else if (songTitle.isEmpty() == true){
                                songTitle = "Unknown"
                            }
                            else if(artistName.isEmpty() == true){
                                artistName = "Unknown"
                            }
                            val albumObject= StringArray(songId,songTitle,artistName,albumTitle)
                            dbHandler.update(albumObject)
                        })
                dialogBuilder.show()
                true
            }
            else -> return super.onContextItemSelected(item)
        }
        adapter.notifyDataSetChanged()
    }
}