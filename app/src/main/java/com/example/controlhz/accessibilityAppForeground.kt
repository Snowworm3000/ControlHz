package com.example.controlhz

import android.accessibilityservice.AccessibilityService
import android.content.ContentResolver
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
                    var listApps = getListApps()
                    if (listApps != null) {
                        listApps.foregroundChanged(event.packageName as String)
                    } else {
                        Log.d(null, "listApps not initialised")
                    }
                }else{
                    Log.d(null, "package returned null")
                }
            }
        }
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("Accessibility", "Service Connected")

        cResolver = contentResolver
        sSharedInstance = this;
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun changeFramerate(value:Int){
        if(getRefreshRate() != null) {
            if(value != null){
                Log.d(null, Settings.Global.AIRPLANE_MODE_ON + " testing 😏")
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

    fun getListApps(): ListApps? {
        return ListApps.getSharedInstance()
    }


    companion object {
        public lateinit var sSharedInstance: accessibilityAppForeground

        public fun getSharedInstance(): accessibilityAppForeground {
            return sSharedInstance
        }
    }
}