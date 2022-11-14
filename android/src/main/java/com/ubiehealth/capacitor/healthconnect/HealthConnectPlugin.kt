package com.ubiehealth.capacitor.healthconnect

import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
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
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
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
                val records = result.records.map { it.toJSONObject() }.toJSONArray()
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
    fun getChanges(call: PluginCall) {
        this.activity.lifecycleScope.launch {
            var token = requireNotNull(call.getString("token"))
            val changes = flow {
                do {
                    val result = healthConnectClient.getChanges(
                            changesToken = token,
                    )
                    emit(result.changes)
                    token = result.nextChangesToken
                } while (result.hasMore)
            }.toList().flatten()

            val res = JSObject().apply {
                put("changes", changes.map { it.toJSObject() }.toJSONArray())
                put("nextToken", token)
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
                put("read", readPermissions.toJSONArray())
                put("write", writePermissions.toJSONArray())
            }
            put("grantedPermissions", grantedPermissions)
            put("hasAllPermissions", hasAllPermissions)
        }
        call.resolve(res)
    }
}
