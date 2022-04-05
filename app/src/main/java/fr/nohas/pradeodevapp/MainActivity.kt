package fr.nohas.pradeodevapp


import android.app.admin.SecurityLog
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

//import virustotalapi.VirusTotal

class MainActivity : AppCompatActivity() {

    private lateinit var textView :TextView
    private lateinit var bttAppsInstalled :Button
    private lateinit var listeView :ListView
    @JvmField val TAG_APP_PROCESS_START=SecurityLog.TAG_APP_PROCESS_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bttAppsInstalled = findViewById(R.id.bt_appInstalled)
        listeView = findViewById(R.id.main_liste_view)
        textView = findViewById(R.id.et_number)

        bttAppsInstalled.setOnClickListener{
            buttonGetInstalledAppsList();
        }
    }
    fun buttonGetInstalledAppsList (){
        val listApplicationInfo : List<ApplicationInfo>  = packageManager.getInstalledApplications(PackageManager.GET_META_DATA) //liste de tt les app installer

        val stringList : ArrayList<String> = ArrayList<String>(listApplicationInfo.size) // sert pour l'affichage
        val pathList : ArrayList<String> = ArrayList<String>()
        textView.setText("Nombre d'application installer= "+listApplicationInfo.size)

        for(app in listApplicationInfo){
                stringList.add(app.packageName)
                pathList.add(app.sourceDir)
            System.out.println("LA "+calculateMD5(app.sourceDir))
        }

        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,stringList)
        listeView.adapter=adapter

    }


    private fun calculateMD5(pathFile :String): String? {
        val file: File = File(pathFile)
        val digest: MessageDigest
        digest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            return null
        }
        val `is`: InputStream
        try {
            `is` = FileInputStream(file)
        } catch (e: FileNotFoundException) {
            return null
        }
        val buffer = ByteArray(`is`.available())
        var read: Int
        return try {
            while (`is`.read(buffer).also { read = it } > 0) {
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
                `is`.close()
            } catch (e: IOException) {
            }
        }
    }
    
    private fun testVirusTotal(){
        
    }
}