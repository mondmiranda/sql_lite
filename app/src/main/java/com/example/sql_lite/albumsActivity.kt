package com.example.sql_lite

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import com.example.sqlite.adapter.gridAdapter


class albumsActivity : AppCompatActivity(){
    lateinit var gridview : GridView
    var arrayList = mutableListOf<albumArr>()
    lateinit var albumTitle : String
    lateinit var relDate : String
    lateinit var adapter: gridAdapter
    val dbHandler = AlbumTableHandler (this)
    val dbSongAlbum = albumSongHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)
        setTitle("Song Albms")
        arrayList = dbHandler.readAll()
        gridview = findViewById(R.id.albumGrid)
        adapter = gridAdapter(this, arrayList)
        gridview.adapter = adapter
        gridview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, l ->
                val album_title = arrayList[position].title
                val tableName = album_title.toString()
                dbSongAlbum.tbleName(tableName)
                startActivity(Intent(this, albumSongActivity::class.java))
            }
        registerForContextMenu(gridview)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.album_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_album -> {
                addDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    fun addDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add a new Album")
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.add_album, null)
        dialogBuilder.setView(view)
            .setTitle("Add Album")
            .setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialogInterface, i ->

            })
            .setPositiveButton("Add Album", DialogInterface.OnClickListener{
                    dialogInterface, i ->
                val inputTitle = view.findViewById<EditText>(R.id.albumTitle)
                val inputDate = view.findViewById<EditText>(R.id.releaseDate)
                albumTitle = inputTitle.text.toString()
                relDate = inputDate.text.toString()
                if (albumTitle.isEmpty() == true){
                    albumTitle = "Unknown"
                }
                else if (relDate.isEmpty() == true){
                    relDate = "Unknown"
                }
                val albumObject= albumArr(title = albumTitle,date = relDate)
                dbSongAlbum.tbleName(albumTitle)
                dbHandler.create(albumObject)
            })
        adapter.notifyDataSetChanged()
        return dialogBuilder.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.album_edit_delete, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val albumPosition = info.position
        val album_id = arrayList[albumPosition].id
        val album_title = arrayList[albumPosition].title
        val album_reldate = arrayList[albumPosition].date
        dbHandler.readOne(album_id)
        return when (item.itemId) {
            R.id.albumEdit ->{
                val inflater = this.layoutInflater
                val dialogBuilder = AlertDialog.Builder(this)
                val view = inflater.inflate(R.layout.edit_album_text, null)
                val inputTitle = view.findViewById<EditText>(R.id.albumTitle)
                val inputDate = view.findViewById<EditText>(R.id.releaseDate)
                inputTitle.setText(album_title)
                inputDate.setText(album_reldate)
                dialogBuilder.setView(view)
                    .setTitle("Edit $album_title")
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialogInterface, i ->

                    })
                    .setPositiveButton("Edit Album", DialogInterface.OnClickListener{
                            dialogInterface, i ->
                        albumTitle = inputTitle.text.toString()
                        relDate = inputDate.text.toString()
                        if (albumTitle.isEmpty() == true){
                            albumTitle = "Unknown"
                        }
                        else if (relDate.isEmpty() == true){
                            relDate = "Unknown"
                        }
                        val albumObject= albumArr(id = album_id,title = albumTitle,date = relDate)
                        dbHandler.update(albumObject)
                    })
                dialogBuilder.show()
                true
            }
            R.id.albumDelete ->{
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Delete? $album_title")
                    .setNegativeButton("No", DialogInterface.OnClickListener{
                            dialogInterface, i ->

                    })
                    .setPositiveButton("Yes", DialogInterface.OnClickListener{
                            dialogInterface, i ->
                        val albumObject= albumArr(id = album_id,title = album_title,date = album_reldate)
                        dbHandler.delete(albumObject)
                    })
                dialogBuilder.show()
                true
            }
            else -> return super.onContextItemSelected(item)
        }
        adapter.notifyDataSetChanged()
    }

}



