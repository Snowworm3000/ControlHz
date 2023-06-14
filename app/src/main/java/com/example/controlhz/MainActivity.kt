package com.example.controlhz

import android.content.ContentResolver
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var cResolver :ContentResolver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cResolver = getContentResolver();
        setContentView(R.layout.activity_main)

        sSharedInstance = this
        updateSwitch()
        configureSwitch()
    }

    override fun onResume() {
        super.onResume()
        updateSwitch()
        Log.d(null, "resume")
    }

    fun configureSwitch(){
        val switch = findViewById<Switch>(R.id.serviceSwitch)
        switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            openSettings(buttonView)
        })

    }
    fun updateSwitch(){
        val switch = findViewById<Switch>(R.id.serviceSwitch)
        switch.isChecked = getAccessibility() != null
    }


    fun getAccessibility(): accessibilityAppForeground? {
        return accessibilityAppForeground.getSharedInstance()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun buttonPress(view: View) {
        Log.d(null, "pressed")
        Log.d(null, """${(cResolver == null)} $cResolver""")
        if(getRefreshRate() != null) {
            Settings.Global.putInt(cResolver, Settings.Global.AIRPLANE_MODE_ON, 1); // If errors occur here it could be that WRITE_SECURE_SETTINGS permission is not given. Give this through adb using "adb shell pm grant com.example.controlhz android.permission.WRITE_SECURE_SETTINGS"
        }else{
            Log.d(null, "cannot set value as it is null which could cause errors.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getRefreshRateView(view:View){
        getRefreshRate()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getRefreshRate(){


        var setting = Settings.Global.getInt(cResolver, Settings.Global.AIRPLANE_MODE_ON)
        Log.d(null, "setting $setting")
    }

    fun openSettings(v: View){
        val openSettings = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS) // Opens accessibility settings so user can enable it
        openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(openSettings)
    }

    fun listApps(v: View){
        var intent = Intent(this, ListApps::class.java)
        startActivity(intent)
    }

    companion object {
        var sSharedInstance: MainActivity? = null

        public fun getSharedInstance(): MainActivity? {
            return sSharedInstance
        }
    }
}