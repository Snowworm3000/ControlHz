package com.example.controlhz

import android.accessibilityservice.AccessibilityService
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import java.lang.NullPointerException
import java.lang.reflect.InvocationTargetException

class accessibilityAppForeground:  AccessibilityService(){
    lateinit var cResolver : ContentResolver

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                if(event.getPackageName() != null) {
                    Log.d("Event", "TYPE_WINDOW_STATE_CHANGED");
                    Log.d("Pkg", event.getPackageName().toString());
//                    var listApps = getListApps()
//                    if (listApps != null) {
                        foregroundChanged(event.packageName as String)
//                    } else {
//                        Log.d(null, "listApps not initialised")
//                    }
                }else{
                    Log.d(null, "package returned null")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun foregroundChanged(pack: String){
        var Hz = getValueFromStorage(pack)
        Log.d(null, "Foreground changed $pack ${Hz}")
        if(Hz != null) {
            changeFramerate(Hz)
        }else{
            changeFramerate(2) //Change to default value (auto)
        }

    }

    fun getValueFromStorage(pack:String): Int {
        val sharedPref = getSharedPreferences(pack, Context.MODE_PRIVATE)

        val mySetting = sharedPref.getInt(pack, -1)
//        if(mySetting != -1) {
////            Log.d(null, "setting $mySetting 🥺")
//        }
        return mySetting
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(null, "destroyed")
        sSharedInstance = null
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("Accessibility", "Service Connected")

        cResolver = contentResolver
        sSharedInstance = this;

        getMain()?.updateSwitch()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun changeFramerate(value:Int){
        if(getRefreshRate() != null) {
            if(value != null){
//                Log.d(null, Settings.Global.AIRPLANE_MODE_ON + " testing 😏")
                Settings.Global.putInt(cResolver, "oneplus_screen_refresh_rate", value); // If errors occur here it could be that WRITE_SECURE_SETTINGS permission is not given. Give this through adb using "adb shell pm grant com.example.controlhz android.permission.WRITE_SECURE_SETTINGS"
            }
        }else{
            Log.d(null, "cannot set value as it is null which could cause errors.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getRefreshRate(): Int? {
        var setting: Int? = try {
            Settings.Global.getInt(cResolver, "oneplus_screen_refresh_rate")
        } catch (e: InvocationTargetException){
            null
        }
        return setting
        Log.d(null, "setting $setting")
    }
//    fun getRefreshRate(): Int? {
//        return null
//    }


    fun getListApps(): ListApps? {
        return ListApps.getSharedInstance()
    }
    fun getMain(): MainActivity? {
        return MainActivity.getSharedInstance()
    }


    companion object {
        var sSharedInstance: accessibilityAppForeground? = null

        public fun getSharedInstance(): accessibilityAppForeground? {
            return sSharedInstance
        }
    }
}