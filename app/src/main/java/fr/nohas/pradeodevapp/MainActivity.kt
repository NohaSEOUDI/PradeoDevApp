package fr.nohas.pradeodevapp

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kanishka.virustotal.dto.VirusScanInfo
import com.kanishka.virustotal.exception.APIKeyNotFoundException
import com.kanishka.virustotal.exception.UnauthorizedAccessException
import com.kanishka.virustotalv2.VirusTotalConfig
import com.kanishka.virustotalv2.VirustotalPublicV2
import com.kanishka.virustotalv2.VirustotalPublicV2Impl
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    private lateinit var bttScan :Button
    private lateinit var bttAppsInstalled :Button
    private lateinit var horizontalPB :ProgressBar
    private var count : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bttAppsInstalled = findViewById(R.id.bt_appInstalled)
        bttScan = findViewById(R.id.bt_scan)
        horizontalPB = findViewById(R.id.progressBarH)


        bttAppsInstalled.setOnClickListener{
           val intent1 = Intent(this, AffichageActivity::class.java) //buttonGetInstalledAppsList();
            startActivity(intent1)
        }

        //button qui déclenche la service
        bttScan.setOnClickListener {
            Toast.makeText(this, "L'annalyse commence...", Toast.LENGTH_SHORT).show()


            horizontalPB.visibility = View.VISIBLE
            val timer= Timer()
            val intent = Intent(this, AnnalyseActivity::class.java)
            timer.schedule(object : TimerTask(){
               override fun run(){
                   count++
                   horizontalPB.progress=count
                   if(count > 100){
                       timer.cancel()
                       startActivity(intent)
                       finish()
                   }
               }
            },0,100)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(Intent(this, MyService2::class.java))
            } else {
                this.startService(Intent(this, MyService2::class.java))
            }
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, MyService::class.java))
        } else {
            this.startService(Intent(this, MyService::class.java))
        }*/
    }
}


