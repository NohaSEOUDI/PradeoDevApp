package fr.nohas.pradeodevapp

import android.R
import android.app.*
import android.content.Intent
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

//service1 qui s'occupe de la génération de "data.txt"
//"data.txt" contient  les hashs de chaque application installée

class MyService : Service() {
    val TAG = "Myservice"
    val fileName="data.txt"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG,"onCreate")
        super.onCreate()
        startForeground()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStratCommand")
        val runnable= Runnable {
           calcul() // fonction qui s'occupe du hachage
           stopSelf()
       }
        val thread=Thread(runnable)
        thread.start()
        return super.onStartCommand(intent, flags, startId)
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
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
    }


    fun calcul(){
        val myFile :FileOutputStream = openFileOutput(fileName,Context.MODE_PRIVATE)
        val listApplicationInfo : List<ApplicationInfo>  = packageManager.getInstalledApplications(PackageManager.GET_META_DATA) //On récupére la liste des applications installées
        val stringPath : ArrayList<String> = ArrayList<String>(listApplicationInfo.size) // sert pour l'affichage

        for(app in listApplicationInfo){
            stringPath.add(app.sourceDir)
            myFile.write((calculateMD5(app.sourceDir)+"\n").toByteArray())
        }
        println("writed to file")
        myFile.close()
    }



    private fun calculateMD5(pathFile :String): String? {
        val file = File(pathFile)
        val digest: MessageDigest
        digest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            return null
        }
        val fileinputStream: InputStream
        try {
            fileinputStream = FileInputStream(file)
        } catch (e: FileNotFoundException) {
            return null
        }
        val buffer = ByteArray(fileinputStream.available()) // pour avoir le nombre de bytes
        var read: Int
        return try {
            while (fileinputStream.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
            val md5sum: ByteArray = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            var output: String = bigInt.toString(16)
            output = String.format("%32s", output).replace(' ', '0')
            output
        } catch (e: IOException) {
            throw RuntimeException("Unable to process file for MD5", e)
        } finally {
            try {
                fileinputStream.close()
            } catch (e: IOException) {
            }
        }
    }


}