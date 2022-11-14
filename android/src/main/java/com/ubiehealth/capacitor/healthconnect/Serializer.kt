package com.ubiehealth.capacitor.healthconnect

import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.impl.converters.datatype.RECORDS_CLASS_NAME_MAP
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Mass
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneOffset

fun <T> JSONArray.toList(): List<T> {
    return (0 until this.length()).map {
        @Suppress("UNCHECKED_CAST")
        this.get(it) as T
    }
}
fun <T> List<T>.toJSONArray(): JSONArray {
    return JSONArray(this)
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

fun Change.toJSObject(): JSObject {
    return JSObject().also { obj ->
        when (this) {
            is UpsertionChange -> {
                obj.put("type", "Upsert")
                obj.put("record", this.record.toJSONObject())
            }
            is DeletionChange -> {
                obj.put("type", "Delete")
                obj.put("recordId", this.recordId)
            }
        }
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
    return this.optJSONArray(name)?.toList<String>()?.map { DataOrigin(it) }?.toSet() ?: emptySet()
}
