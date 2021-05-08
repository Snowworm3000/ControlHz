package com.example.controlhz

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ListApps : AppCompatActivity(), RecyclerViewAdapter.ItemClickListener {
    var configuredApps: MutableMap<String, Int> = mutableMapOf()

    var adapter: RecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_apps)

//        val applications :  List<Map<String, Any>>  = listInstalledApps()
        val applications :  MutableList<Map<String, Any>>  = mutableListOf()


        // set up the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.listApps)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerViewAdapter(this, applications, configuredApps)
        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter

//        adapter?.addItem(0, mapOf<String,Any>("name" to "first"))
//        adapter?.addItem(0, mapOf<String,Any>("name" to "second"))
//        adapter?.addItem(0, mapOf<String,Any>("name" to "third"))
        LongOperation().execute()


        sSharedInstance = this


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun foregroundChanged(pack: String){
        Log.d(null, "Foreground changed $pack ${configuredApps[pack]}")

        if(configuredApps[pack] != null) {
            getAccessibility().changeFramerate(configuredApps[pack]!!)
        }else{
            getAccessibility().changeFramerate(2) //Change to default value (auto)
        }

    }

    companion object {
        var sSharedInstance: ListApps? = null

        public fun getSharedInstance(): ListApps? {
            return sSharedInstance
        }
    }

    fun getAccessibility(): accessibilityAppForeground {
        return accessibilityAppForeground.getSharedInstance()
    }

    override fun onItemClick(view: View?, position: Int) {
//        Toast.makeText(
//            this,
//            "You clicked " + adapter?.getItem(position).toString() + " on row number " + position,
//            Toast.LENGTH_SHORT
//        ).show()

        var options: List<Int> = listOf(2, 0, 1)  // 2 = auto, 0 = 90Hz, 1 = 60Hz
        if(adapter != null) {
            var item = adapter!!.getItem(position)
            if(configuredApps.containsKey(item)){
//                Log.d(null, "before ${configuredApps[item]}")
                configuredApps[item] = options[configuredApps[item]!!]
//                Log.d(null, "after ${configuredApps[item]}")

                addValueToStorage(item, configuredApps[item]!!)
            }else {
                configuredApps.put(item, 1) // Default first value

                addValueToStorage(item, configuredApps[item]!!)
            }
//            Log.d(null, configuredApps[item].toString() + "    " + configuredApps.toString())

            adapter!!.updateItem(position, item, configuredApps[item]!!)
        }else{
            Log.d(null, "error adapter null")
        }


    }

    fun addValueToStorage(key:String,value:Int){
        val sharedPref = getSharedPreferences(key, Context.MODE_PRIVATE)
//        Log.d(null, "🤪 add $key $value")
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getValueFromStorage(pack:String): Int {
        val sharedPref = getSharedPreferences(pack, Context.MODE_PRIVATE)

        val mySetting = sharedPref.getInt(pack, -1)
//        if(mySetting != -1) {
////            Log.d(null, "setting $mySetting 🥺")
//        }
        return mySetting
    }

    inner class LongOperation : AsyncTask<Void?, Any, String>() {
        override fun doInBackground(vararg params: Void?): String {
//            for (i in 0..4) {
//                try {
//                    Thread.sleep(1000)
//                } catch (e: InterruptedException) {
//                    // We were cancelled; stop sleeping!
//                }
//            }
//            publishProgress("first", "hello")

            val pm = packageManager
//get a list of installed apps.
//get a list of installed apps.
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

//        val applications : ArrayList<String> = ArrayList()
            val applications : ArrayList<Map<String, Any>> = ArrayList()
            for (packageInfo in packages) {
//            Log.d(TAG, "Installed package :" + packageInfo.packageName)
//            Log.d(TAG, "Source dir : " + packageInfo.sourceDir)
//            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
//            Log.d(TAG, "App name :" + pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA)))
                if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {

                    if(adapter != null){
//                        Log.d(null, "add item 🤨")

                        publishProgress(pm,applications,packageInfo)
                    }else{
                        Log.d(null, "error adapter null applications")
                    }
                }
            }



            return "Executed"
        }

        override fun onProgressUpdate(vararg values: Any) {
            super.onProgressUpdate(*values)
//            adapter?.addItem(0, mapOf<String,Any>("name" to "test"))
            var appInfo = getAppInfo(values[0] as PackageManager, values[1] as ArrayList<Map<String, Any>>, values[2] as ApplicationInfo)
            adapter!!.addItem(appInfo[1] as Int, appInfo[0] as Map<String, Any>)
//            Log.d(null, "added ${values[0].toString()} ${values}")
        }

        override fun onPostExecute(result: String) {
//            val txt = findViewById<View>(R.id.output) as TextView
//            txt.text = "Executed" // txt.setText(result);
            // You might want to change "executed" for the returned string
            // passed into onPostExecute(), but that is up to you
        }



    }

    fun getAppInfo(pm:PackageManager, applications:ArrayList<Map<String, Any>>, packageInfo: ApplicationInfo): Array<Any> {
        var name = pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA))
        var app = mapOf("name" to name, "image" to packageInfo.loadIcon(packageManager), "package" to packageInfo.packageName)
        applications.add(app)
        var Hz = getValueFromStorage(packageInfo.packageName)
        if(Hz != -1) {
            configuredApps.put(packageInfo.packageName, Hz)
        }
//        Log.d(null, "configured apps $configuredApps")

        var sortedApps = applications.sortedWith(compareBy({ it.get("name").toString().toLowerCase() }))
//        Log.d(null, "sorted ${sortedApps}")
        var index = sortedApps.indexOf(app)

        return arrayOf<Any>(applications[applications.size - 1], index)

    }


    fun listInstalledApps(): ArrayList<Map<String, Any>> {
        val pm = packageManager
//get a list of installed apps.
//get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

//        val applications : ArrayList<String> = ArrayList()
        val applications : ArrayList<Map<String, Any>> = ArrayList()

        var TAG = "App: "


        for (packageInfo in packages) {
//            Log.d(TAG, "Installed package :" + packageInfo.packageName)
//            Log.d(TAG, "Source dir : " + packageInfo.sourceDir)
//            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
//            Log.d(TAG, "App name :" + pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA)))
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null){
//            val intent = Intent()
//            intent.setPackage(packageInfo.packageName)
//            Log.d(TAG, "Activity: ${pm.queryIntentActivities(intent, 0) }")
//            if (pm.queryIntentActivities(intent, 0) != listOf<>()){
                var name = pm.getApplicationLabel(pm.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA))
                applications.add(mapOf("name" to name, "image" to packageInfo.loadIcon(packageManager), "package" to packageInfo.packageName))
                var Hz = getValueFromStorage(packageInfo.packageName)
                if(Hz != -1) {
                    configuredApps.put(packageInfo.packageName, Hz)
                }
//                Log.d(TAG, "configured apps $configuredApps")

                if(adapter != null){
//                    Log.d(null, "add item 🤨")
                    adapter!!.addItem(0, applications[applications.size -1])
                }else{
                    Log.d(null, "error adapter null applications")
                }
            }
        }
        return applications
    }


}