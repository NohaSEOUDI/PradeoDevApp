package fr.nohas.pradeodevapp

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AffichageActivity : AppCompatActivity() {
    private lateinit var listView1 : ListView
    private lateinit var textView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affichage)
        listView1=findViewById(R.id.liste_view)
        textView=findViewById(R.id.text_view)

       //On récupére la liste des applications installées
        val listApplication : List<PackageInfo>  = packageManager.getInstalledPackages(0)
        val stringList : ArrayList<String> = ArrayList<String>(listApplication.size) // sert pour l'affichage

        textView.setText("Nombre d'application installer= "+listApplication.size)

        var strName:String
         for(app in listApplication){
             strName =packageManager.getApplicationLabel(app.applicationInfo).toString()
             stringList.add(strName)
         }

        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,stringList)
        listView1.adapter=adapter

    }

}