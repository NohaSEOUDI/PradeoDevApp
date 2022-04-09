package fr.nohas.pradeodevapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class AnnalyseActivity : AppCompatActivity() {
    private lateinit var textView : TextView
    private lateinit var updateReciver : MyReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume(){
        super.onResume()
        updateReciver = MyReceiver()
        var intentFilter :IntentFilter = IntentFilter()
        intentFilter.addAction("message")
       registerReceiver(updateReciver, intentFilter)


    }

    override fun onPause(){
        super.onPause()
        unregisterReceiver(updateReciver)
    }

    private class MyReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
             var datapassed: Int? = intent?.getIntExtra("number",0)
            Toast.makeText(context, "Data passed:"+datapassed.toString(), Toast.LENGTH_SHORT).show()
        }

    }

}