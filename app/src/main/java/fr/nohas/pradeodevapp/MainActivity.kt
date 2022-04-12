package fr.nohas.pradeodevapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kanishka.virustotal.dto.VirusScanInfo
import com.kanishka.virustotal.exception.APIKeyNotFoundException
import com.kanishka.virustotal.exception.UnauthorizedAccessException
import com.kanishka.virustotalv2.VirusTotalConfig
import com.kanishka.virustotalv2.VirustotalPublicV2
import com.kanishka.virustotalv2.VirustotalPublicV2Impl
import fr.nohas.pradeodevapp.R.layout
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    private lateinit var textView :TextView
    private lateinit var image: ImageView
    private lateinit var bttScan :Button
    private lateinit var bttAppsInstalled :Button
    private lateinit var bttHashFile :Button
    private lateinit var horizontalPB2 :ProgressBar

    private var count2 : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        textView=findViewById(R.id.text_view)
        bttAppsInstalled = findViewById(R.id.bt_appInstalled)
        bttScan = findViewById(R.id.bt_scan)
        image=findViewById(R.id.image_view)
        horizontalPB2 = findViewById(R.id.progressBarH2)
        bttHashFile = findViewById(R.id.bt_hashFile)


        //1er button
        bttAppsInstalled.setOnClickListener{
           val intent1 = Intent(this, AffichageActivity::class.java) //buttonGetInstalledAppsList();
            startActivity(intent1)
        }

        //2èm button qui déclenche la service
        bttScan.setOnClickListener {
            Toast.makeText(this, "L'annalyse commence, Veuillez patienter....", Toast.LENGTH_SHORT).show()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(Intent(this, MyService2::class.java))
            } else {
                this.startService(Intent(this, MyService2::class.java))
            }
        }
        //3ème button qui déclenche service2
        bttHashFile.setOnClickListener {
            Toast.makeText(this, "On scan vos données, Veuillez patienter...", Toast.LENGTH_SHORT).show()
            horizontalPB2.visibility = View.VISIBLE
            val timer= Timer()
            timer.schedule(object : TimerTask(){
                override fun run(){
                    count2++
                    horizontalPB2.progress=count2
                    if(count2 > 100){
                        timer.cancel()
                    }
                }
            },0,200)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(Intent(this, MyService::class.java))
            } else {
                this.startService(Intent(this, MyService::class.java))
            }
        }

    }

    //mise en place du broadcastReciver pour commuiquer avec le service
    private val reciver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            var message: Int? = p1?.getIntExtra("sumPositive",0)
            if (message != null && message==0){
                textView.setText("Votre niveau de risque est de "+(message*100)/100+"%")
               image.setBackgroundResource(R.drawable.valide)
            }else{
                image.setBackgroundResource(R.drawable.nonvalide)
            }
        }

    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(reciver, IntentFilter("Action"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciver)
    }
}


