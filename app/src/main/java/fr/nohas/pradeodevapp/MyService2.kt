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
import android.os.Bundle
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
import java.io.UnsupportedEncodingException

class MyService2 : Service() {
    val TAG = "Myservice2"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        Log.d(TAG,"onCreate")
        super.onCreate()
        startForeground()
        // calcul()
    }
    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStratCommand")
        val runnable= Runnable {
            getFileScanReport()
            stopSelf()
        }
        val thread=Thread(runnable)
        thread.start()
        return super.onStartCommand(intent, flags, startId)
    }
    fun getFileScanReport() {
        var isPositive : Int  = 0
        var niveauxRisque = 0
        try {
            VirusTotalConfig.getConfigInstance().virusTotalAPIKey = "918a101ad416e878a01a5ec5c2ee0ed06215db8ba0b09e28cc70b7b08a218525"
            val virusTotalRef: VirustotalPublicV2 = VirustotalPublicV2Impl()

            val resource ="4fd9275fce5dee2d5c1ac52e2fd0e303"  // "4fd9275fce5dee2d5c1ac52e2fd0e303"
            val report = virusTotalRef.getScanReport(resource)
            println("Positives :\t" + report.positives)
            println("Total :\t" + report.total)
            if(report.positives == 1)
                isPositive++

            niveauxRisque=isPositive/100
            println("Niveau de risque :\t"+niveauxRisque)

            val intent: Intent=Intent("message")
           // var bundle = Bundle()
           // bundle.putString("keyValue", niveauxRisque.toString())
            intent.putExtra("number",niveauxRisque)
           // intent.putExtra("successB",bundle)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

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
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
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