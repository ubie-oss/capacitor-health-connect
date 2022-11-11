package com.ubiehealth.capacitor.healthconnect

import androidx.activity.result.ActivityResult
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.impl.converters.datatype.RECORDS_TYPE_NAME_MAP
import androidx.health.connect.client.impl.converters.permission.toProtoPermission
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.WeightRecord
import androidx.health.platform.client.proto.PermissionProto
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import kotlin.reflect.KClass

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
    fun requestHealthPermissions(call: PluginCall) {
        val readPermissions = call.getArray("read").toList<String>().map {
            HealthPermission.createReadPermission(
                recordType = RECORDS_TYPE_NAME_MAP[it] ?: throw IllegalArgumentException("Unexpected RecordType: $it")
            )
        }.toSet()
        val writePermissions = call.getArray("write").toList<String>().map {
            HealthPermission.createWritePermission(
                recordType = RECORDS_TYPE_NAME_MAP[it] ?: throw IllegalArgumentException("Unexpected RecordType: $it")
            )
        }.toSet()

        val intent = permissionContract.createIntent(
            context = this.context,
            input = readPermissions + writePermissions
        )

        startActivityForResult(call, intent, "handleRequestPermission")
    }

    @ActivityCallback
    fun handleRequestPermission(call: PluginCall, result: ActivityResult) {
        val permissions = permissionContract.parseResult(result.resultCode, result.data)
            .map { it.toProtoPermission() }
                .toSet()
        val readPermissions = permissions
            .filter { it.accessType == PermissionProto.AccessType.ACCESS_TYPE_READ }
            .map { it.dataType.name }
        val writePermissions = permissions
            .filter { it.accessType == PermissionProto.AccessType.ACCESS_TYPE_WRITE }
            .map { it.dataType.name }

        val hasAllPermissions = readPermissions.containsAll(call.getArray("read").toList())
            && writePermissions.containsAll(call.getArray("write").toList())


        val res = JSObject().apply {
            val grantedPermissions = JSObject().apply {
                put("read", readPermissions.toJSArray())
                put("write", writePermissions.toJSArray())
            }
            put("grantedPermissions", grantedPermissions)
            put("hasAllPermissions", hasAllPermissions)
        }
        call.resolve(res)
    }
}

fun <T> List<T>.toJSArray(): JSArray {
    val jsArray = JSArray()
    this.forEach {
        jsArray.put(it)
    }

    return jsArray
}
