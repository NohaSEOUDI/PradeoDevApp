package fr.nohas.pradeodevapp

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kanishka.virustotal.exception.APIKeyNotFoundException
import com.kanishka.virustotal.exception.UnauthorizedAccessException
import com.kanishka.virustotalv2.VirusTotalConfig
import com.kanishka.virustotalv2.VirustotalPublicV2
import com.kanishka.virustotalv2.VirustotalPublicV2Impl
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException

//Service 2 qui utilise les fonction de l'api virusTotal
class MyService2 : Service() {
    val TAG = "Myservice2"
    var isPositive:Int = 0


    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        Log.d(TAG,"onCreate")
        super.onCreate()
        startForeground()

    }
    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStratCommand")
        val runnable= Runnable {
            getFileScanReport()
            goToActivity()
            stopSelf()
        }
        val thread=Thread(runnable)
        thread.start()


        return super.onStartCommand(intent, flags, startId)
    }
    //méthode sert à faire la communication avec l'activity
    private fun goToActivity(){
        val dialog = Intent("Action")//this, MainActivity::class.java)
        dialog.putExtra("sumPositive",isPositive)
       LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(dialog)
    }

    //fonction de scan de l'api VirusTotal
    fun getFileScanReport() {

        var resource:String
        try {
            VirusTotalConfig.getConfigInstance().virusTotalAPIKey = "918a101ad416e878a01a5ec5c2ee0ed06215db8ba0b09e28cc70b7b08a218525"
            val virusTotalRef: VirustotalPublicV2 = VirustotalPublicV2Impl()

            //pour ouvrir le fichier
            val file: FileInputStream = openFileInput("data.txt") //1
            val inputStreamReader:InputStreamReader = InputStreamReader(file) //2
            val bufferedReader : BufferedReader = BufferedReader(inputStreamReader) //3

           // pour lire ligne par ligne car chaque ligne de notre fichier data.txt correspond à une appli
            var line: String=bufferedReader.readLine()

            while(line!=null){
                resource = line
                println(resource)
                val report = virusTotalRef.getScanReport(resource)
               var positive= report.positives
                var sum=report.total
                var verbose=report.verboseMessage

               if(positive==1)
                   isPositive++//"virus a été détéctée"

                println("Positives :\t" + positive)
                println("Total :\t" + sum)
            }

        } catch (ex: APIKeyNotFoundException) {
            System.err.println("API Key not found! " + ex.message)
        } catch (ex: UnsupportedEncodingException) {
            System.err.println("Unsupported Encoding Format!" + ex.message)
        } catch (ex: UnauthorizedAccessException) {
            System.err.println("Invalid API Key " + ex.message)
        } catch (ex: Exception) {
            System.err.println("Something Bad Happened! " + ex.message)
        }
    }

    //Solution pour l'erreur que j'ai eu de RuntimeException
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

}