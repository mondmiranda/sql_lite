package com.example.sql_lite

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sqlite.adapter.listAdapter
import java.nio.file.Files.delete

class QueuedSongsActivity : AppCompatActivity() {
    lateinit var adapter: listAdapter
    var list = mutableListOf<stringArray>()
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test Notification"
    val dbqueueHandler = queueTableHandler(this)
    lateinit var songTitle : String
    lateinit var artistName : String
    lateinit var albumTitle : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queued_songs)
        setTitle("Queued Songs")
        val queueList = findViewById<ListView>(R.id.queued_list)
        list = dbqueueHandler.readAll()
        adapter = listAdapter(this,R.layout.main_row, list)
        if (adapter.count == 0){
            notification()
        }
        queueList.adapter = adapter
        registerForContextMenu(queueList)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val songPosition = info.position
        val songId = list[songPosition].id
        dbqueueHandler.readOne(songId)
        songTitle = list[songPosition].songName
        artistName = list[songPosition].artistName
        albumTitle = list[songPosition].albumName
        val song = stringArray(songId,songName = songTitle,artistName = artistName,albumName = albumTitle)
        return when(item.itemId) {
            R.id.remove -> {
                val dialogBuilder = AlertDialog.Builder(this)
                        .setTitle("Delete? $songTitle")
                        .setNegativeButton("No", DialogInterface.OnClickListener{
                            dialogInterface, i ->

                        })
                        .setPositiveButton("Yes", DialogInterface.OnClickListener{
                            dialogInterface, i ->
                            val albumObject= stringArray(songId, songTitle, artistName,albumTitle)
                            dbqueueHandler.delete(albumObject)
                            Toast.makeText(this, R.string.delete, Toast.LENGTH_SHORT).show()
                        })
                dialogBuilder.show()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun notification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =  PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,description, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableVibration(false)
            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder (this, channelId)
                    .setContentTitle("Notification")
                    .setContentText("QUEUE IS EMPTY")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
        }
        else{
            builder = Notification.Builder (this)
                    .setContentTitle("Notification")
                    .setContentText("QUEUE IS EMPTY")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
        }
        notificationManager.notify(1234,builder.build())
    }
}