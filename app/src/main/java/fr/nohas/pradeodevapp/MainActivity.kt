package fr.nohas.pradeodevapp


import android.app.admin.SecurityLog
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

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

    }
    fun buttonGetInstalledAppsList (){
        val listApplicationInfo :List<ApplicationInfo>  = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val stringList :ArrayList<String> = ArrayList<String>(listApplicationInfo.size)
        textView.setText("Nombre d'application installer= "+listApplicationInfo.size)
        for(app in listApplicationInfo){
                stringList.add(app.packageName)
        }

        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,stringList)
        listeView.adapter=adapter

    }
}