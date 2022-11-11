package com.ubiehealth.capacitor.healthconnect

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.WeightRecord
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "HealthConnectPlugin")
class HealthConnectPluginPlugin : Plugin() {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this.context) }
    private val permissionContract by lazy {
        PermissionController.createRequestPermissionResultContract()
    }

    private val implementation = HealthConnectPlugin()
    @PluginMethod
    fun echo(call: PluginCall) {
        val value = call.getString("value")
        val ret = JSObject()
        ret.put("value", implementation.echo(value!!))
        call.resolve(ret)
    }

    @PluginMethod
    fun requestPermission(call: PluginCall) {
        val intent = permissionContract.createIntent(
            context = this.context,
            input = setOf(
                HealthPermission.createReadPermission(WeightRecord::class),
                HealthPermission.createWritePermission(WeightRecord::class),
            )
        )

        startActivityForResult(call, intent, "handleRequestPermission")
    }

    @ActivityCallback
    fun handleRequestPermission(call: PluginCall, result: ActivityResult) {
        val permissions = permissionContract.parseResult(result.resultCode, result.data)
        val res = if (permissions.isNotEmpty()) "granted" else "denied"
        val obj = JSObject().apply {
            put("permission", res)
        }
        call.resolve(obj)
    }
}
