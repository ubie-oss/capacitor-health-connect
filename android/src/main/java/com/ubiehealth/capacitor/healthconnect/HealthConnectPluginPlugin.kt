package com.ubiehealth.capacitor.healthconnect

import androidx.activity.result.ActivityResult
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.impl.converters.datatype.RECORDS_TYPE_NAME_MAP
import androidx.health.connect.client.impl.converters.permission.toProtoPermission
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import androidx.health.platform.client.proto.PermissionProto
import androidx.lifecycle.lifecycleScope
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

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
    fun insertRecords(call: PluginCall) {
        this.activity.lifecycleScope.launch {
            val records = call.getArray("records").toList<JSONObject>().map { it.toRecord() }
            val result = healthConnectClient.insertRecords(records)

            val res = JSObject().apply {
                put("recordIds", result.recordIdsList)
            }
            call.resolve(res)
        }
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

fun JSONObject.toRecord(): Record {
    return when (val type = this.get("type")) {
        "Weight" -> WeightRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            weight = this.getMass("weight"),
        )
        "Steps" -> StepsRecord(
            startTime = this.getInstant("startTime"),
            startZoneOffset = this.getZoneOffsetOrNull("startZoneOffset"),
            endTime = this.getInstant("endTime"),
            endZoneOffset = this.getZoneOffsetOrNull("endZoneOffset"),
            count = this.getLong("count"),
        )
        else -> throw IllegalArgumentException("Unexpected record type: $type")
    }
}

fun JSONObject.getInstant(name: String): Instant {
    return Instant.parse(this.getString(name))
}

fun JSONObject.getZoneOffsetOrNull(name: String): ZoneOffset? {
    return if (this.has(name))
        ZoneId.of(this.getString(name)).rules.getOffset(Instant.EPOCH)
    else
        null
}

fun JSONObject.getMass(name: String): Mass {
    val obj = requireNotNull(this.getJSONObject(name))
    val unit = obj.getString("unit")
    val value = obj.getDouble("value")
    return when (unit) {
        "gram" -> Mass.grams(value)
        "kilogram" -> Mass.kilograms(value)
        "milligram" -> Mass.milligrams(value)
        "microgram" -> Mass.micrograms(value)
        "ounce" -> Mass.ounces(value)
        "pound" -> Mass.pounds(value)
        else -> throw IllegalArgumentException("Unexpected mass unit: $unit")
    }
}
