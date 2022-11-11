package com.ubiehealth.capacitor.healthconnect

import android.util.Log

class HealthConnectPlugin {
    fun echo(value: String): String {
        Log.i("Echo", value)
        return value
    }
}
