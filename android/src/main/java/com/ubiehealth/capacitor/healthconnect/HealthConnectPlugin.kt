package com.ubiehealth.capacitor.healthconnect

import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.impl.converters.datatype.RECORDS_CLASS_NAME_MAP
import androidx.health.connect.client.impl.converters.datatype.RECORDS_TYPE_NAME_MAP
import androidx.health.connect.client.impl.converters.permission.toProtoPermission
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
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
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneOffset
import java.util.Date

@CapacitorPlugin(name = "HealthConnect")
class HealthConnectPlugin : Plugin() {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this.context) }
    private val permissionContract by lazy {
        PermissionController.createRequestPermissionResultContract()
    }

    @PluginMethod
    fun checkAvailability(call: PluginCall) {
        val availability = when {
            HealthConnectClient.isAvailable(this.context) -> "Available"
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> "NotInstalled"
            else -> "NotSupported"
        }

        val res = JSObject().apply {
            put("availability", availability)
        }
        call.resolve(res)
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
    fun readRecord(call: PluginCall) {
        this.activity.lifecycleScope.launch {
            val type = call.getString("type").let {
                RECORDS_TYPE_NAME_MAP[it] ?: throw IllegalArgumentException("Unexpected RecordType: $it")
            }

            val result = healthConnectClient.readRecord(
                    recordType = type,
                    recordId = requireNotNull(call.getString("recordId"))
            )

            val res = JSObject().apply {
                this.put("record", result.record.toJSONObject())
            }
            call.resolve(res)
        }
    }

    @PluginMethod
    fun readRecords(call: PluginCall) {
        this.activity.lifecycleScope.launch {
            val type = call.getString("type").let {
                RECORDS_TYPE_NAME_MAP[it] ?: throw IllegalArgumentException("Unexpected RecordType: $it")
            }
            val request = ReadRecordsRequest(
                    recordType = type,
                    timeRangeFilter = call.data.getTimeRangeFilter("timeRangeFilter"),
                    dataOriginFilter = call.data.getDataOriginFilter("dataOriginFilter"),
                    ascendingOrder = call.getBoolean("ascendingOrder") ?: true,
                    pageSize = call.getInt("pageSize") ?: 1000,
                    pageToken = call.getString("pageToken"),
            )
            val result = healthConnectClient.readRecords(request)

            val res = JSObject().apply {
                val records = result.records.map { it.toJSONObject() }.toJSArray()
                this.put("records", records)
                this.put("pageToken", result.pageToken)
            }
            call.resolve(res)
        }
    }

    @PluginMethod
    fun getChangesToken(call: PluginCall) {
        this.activity.lifecycleScope.launch {
            val types = call.getArray("types").toList<String>().map {
                RECORDS_TYPE_NAME_MAP[it] ?: throw IllegalArgumentException("Unexpected RecordType: $it")
            }.toSet()
            val request = ChangesTokenRequest(
                    recordTypes = types,
                    dataOriginFilters = call.data.getDataOriginFilter("dataOriginFilter"),
            )
            val token = healthConnectClient.getChangesToken(request)

            val res = JSObject().apply {
                this.put("token", token)
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

fun Record.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("type", RECORDS_CLASS_NAME_MAP[this::class])
        obj.put("metadata", this.metadata.toJSONObject())

        when (this) {
            is WeightRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("weight", this.weight.toJSONObject())
            }
            is StepsRecord -> {
                obj.put("startTime", this.startTime)
                obj.put("startZoneOffset", this.startZoneOffset?.toJSONValue())
                obj.put("endTime", this.endTime)
                obj.put("endZoneOffset", this.endZoneOffset?.toJSONValue())
                obj.put("count", this.count)
            }
            else -> throw IllegalArgumentException("Unexpected record class: $${this::class.qualifiedName}")
        }
    }
}

fun Metadata.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("id", this.id)
        obj.put("clientRecordId", this.clientRecordId)
        obj.put("clientRecordVersion", this.clientRecordVersion)
        obj.put("lastModifiedTime", this.lastModifiedTime)
        obj.put("dataOrigin", this.dataOrigin.packageName)
    }
}

fun JSONObject.getInstant(name: String): Instant {
    return Instant.parse(this.getString(name))
}

fun JSONObject.getZoneOffsetOrNull(name: String): ZoneOffset? {
    return if (this.has(name))
        ZoneOffset.of(this.getString(name))
    else
        null
}

fun ZoneOffset.toJSONValue(): String {
    return this.id
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

fun Mass.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        // TODO: support other unit
        obj.put("unit", "gram")
        obj.put("value", this.inGrams)
    }
}

fun JSONObject.getTimeRangeFilter(name: String): TimeRangeFilter {
    val obj = requireNotNull(this.getJSONObject(name))
    return when (val type = obj.getString("type")) {
        "before" -> TimeRangeFilter.before(obj.getInstant("time"))
        "after" -> TimeRangeFilter.after(obj.getInstant("time"))
        "between" -> TimeRangeFilter.between(obj.getInstant("startTime"), obj.getInstant("endTime"))
        else -> throw IllegalArgumentException("Unexpected TimeRange type: $type")
    }
}

fun JSObject.getDataOriginFilter(name: String): Set<DataOrigin> {
    return this.optJSONArray("dataOriginFilter")?.toList<String>()?.map { DataOrigin(it) }?.toSet() ?: emptySet()
}

fun <T> JSONArray.toList(): List<T> {
    return (0 until this.length()).map {
        @Suppress("UNCHECKED_CAST")
        this.get(it) as T
    }
}
