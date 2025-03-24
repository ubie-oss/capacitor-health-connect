package com.ubiehealth.capacitor.healthconnect

import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.impl.converters.datatype.RECORDS_CLASS_NAME_MAP
import androidx.health.connect.client.records.*
import androidx.health.connect.client.records.BodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_INT_TO_STRING_MAP
import androidx.health.connect.client.records.BodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_STRING_TO_INT_MAP
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.*
import com.getcapacitor.JSObject
import org.json.JSONArray
import org.json.JSONObject
import java.lang.RuntimeException
import java.time.Instant
import java.time.ZoneOffset

internal fun <T> JSONArray.toList(): List<T> {
    return (0 until this.length()).map {
        @Suppress("UNCHECKED_CAST")
        this.get(it) as T
    }
}

internal fun <T> List<T>.toJSONArray(): JSONArray {
    return JSONArray(this)
}

internal fun JSONObject.toRecord(): Record {
    return when (val type = this.get("type")) {
        "ActiveCaloriesBurned" -> ActiveCaloriesBurnedRecord(
            startTime = this.getInstant("startTime"),
            startZoneOffset = this.getZoneOffsetOrNull("startZoneOffset"),
            endTime = this.getInstant("endTime"),
            endZoneOffset = this.getZoneOffsetOrNull("endZoneOffset"),
            energy = this.getEnergy("energy"),
        )
        "BasalBodyTemperature" -> BasalBodyTemperatureRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            temperature = this.getTemperature("temperature"),
            measurementLocation = this.getBodyTemperatureMeasurementLocationInt("measurementLocation"),
        )
        "BasalMetabolicRate" -> BasalMetabolicRateRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            basalMetabolicRate = this.getPower("basalMetabolicRate"),
        )
        "BloodGlucose" -> BloodGlucoseRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            level = this.getBloodGlucose("level"),
            specimenSource = BloodGlucoseRecord.SPECIMEN_SOURCE_STRING_TO_INT_MAP
                .getOrDefault(this.getString("specimenSource"), BloodGlucoseRecord.SPECIMEN_SOURCE_UNKNOWN),
            mealType = MealType.MEAL_TYPE_STRING_TO_INT_MAP
                .getOrDefault(this.getString("mealType"), MealType.MEAL_TYPE_UNKNOWN),
            relationToMeal = BloodGlucoseRecord.RELATION_TO_MEAL_STRING_TO_INT_MAP
                .getOrDefault(this.getString("relationToMeal"), BloodGlucoseRecord.RELATION_TO_MEAL_UNKNOWN),
        )
        "BloodPressure" -> BloodPressureRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            systolic = this.getPressure("systolic"),
            diastolic = this.getPressure("diastolic"),
            bodyPosition = BloodPressureRecord.BODY_POSITION_STRING_TO_INT_MAP
                .getOrDefault(this.getString("bodyPosition"), BloodPressureRecord.BODY_POSITION_UNKNOWN),
            measurementLocation = BloodPressureRecord.MEASUREMENT_LOCATION_STRING_TO_INT_MAP
                .getOrDefault(this.getString("measurementLocation"), BloodPressureRecord.MEASUREMENT_LOCATION_UNKNOWN),
        )
        "BodyFat" -> BodyFatRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            percentage = this.getPercentage("percentage"),
        )
        "BodyTemperature" -> BodyTemperatureRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            temperature = this.getTemperature("temperature"),
            measurementLocation = this.getBodyTemperatureMeasurementLocationInt("measurementLocation"),
        )
        "Distance" -> DistanceRecord(
            startTime = this.getInstant("startTime"),
            startZoneOffset = this.getZoneOffsetOrNull("startZoneOffset"),
            endTime = this.getInstant("endTime"),
            endZoneOffset = this.getZoneOffsetOrNull("endZoneOffset"),
            distance = this.getLength("distance"),
        )
        "HeartRateSeries" -> HeartRateRecord(
            startTime = this.getInstant("startTime"),
            startZoneOffset = this.getZoneOffsetOrNull("startZoneOffset"),
            endTime = this.getInstant("endTime"),
            endZoneOffset = this.getZoneOffsetOrNull("endZoneOffset"),
            samples = this.getHeartRateRecordSamplesList("samples")
        )
        "Height" -> HeightRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            height = this.getLength("height"),
        )
        "OxygenSaturation" -> OxygenSaturationRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            percentage = this.getPercentage("percentage"),
        )
        "RespiratoryRate" -> RespiratoryRateRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            rate = this.getDouble("rate"),
        )
        "RestingHeartRate" -> RestingHeartRateRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            beatsPerMinute = this.getLong("beatsPerMinute"),
        )
        "Steps" -> StepsRecord(
            startTime = this.getInstant("startTime"),
            startZoneOffset = this.getZoneOffsetOrNull("startZoneOffset"),
            endTime = this.getInstant("endTime"),
            endZoneOffset = this.getZoneOffsetOrNull("endZoneOffset"),
            count = this.getLong("count"),
        )
        "Weight" -> WeightRecord(
            time = this.getInstant("time"),
            zoneOffset = this.getZoneOffsetOrNull("zoneOffset"),
            weight = this.getMass("weight"),
        )
        else -> throw IllegalArgumentException("Unexpected record type: $type")
    }
}

internal fun Record.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("type", RECORDS_CLASS_NAME_MAP[this::class])
        obj.put("metadata", this.metadata.toJSONObject())

        when (this) {
            is ActiveCaloriesBurnedRecord -> {
                obj.put("startTime", this.startTime)
                obj.put("startZoneOffset", this.startZoneOffset?.toJSONValue())
                obj.put("endTime", this.endTime)
                obj.put("endZoneOffset", this.endZoneOffset?.toJSONValue())
                obj.put("energy", this.energy.toJSONObject())
            }
            is BasalBodyTemperatureRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("temperature", this.temperature.toJSONObject())
                obj.put("measurementLocation", this.measurementLocation.toBodyTemperatureMeasurementLocationString())
            }
            is BasalMetabolicRateRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("basalMetabolicRate", this.basalMetabolicRate.toJSONObject())
            }
            is BloodGlucoseRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("level", this.level.toJSONObject())
                obj.put("specimenSource", BloodGlucoseRecord.SPECIMEN_SOURCE_INT_TO_STRING_MAP.getOrDefault(this.specimenSource, "unknown"))
                obj.put("mealType", MealType.MEAL_TYPE_INT_TO_STRING_MAP.getOrDefault(this.mealType, "unknown"))
                obj.put("relationToMeal", BloodGlucoseRecord.RELATION_TO_MEAL_INT_TO_STRING_MAP.getOrDefault(this.relationToMeal, "unknown"))
            }
            is BloodPressureRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("systolic", this.systolic.toJSONObject())
                obj.put("diastolic", this.diastolic.toJSONObject())
                obj.put("bodyPosition", BloodPressureRecord.BODY_POSITION_INT_TO_STRING_MAP.getOrDefault(this.bodyPosition, "unknown"))
                obj.put("measurementLocation", BloodPressureRecord.MEASUREMENT_LOCATION_INT_TO_STRING_MAP.getOrDefault(this.measurementLocation, "unknown"))
            }
            is BodyFatRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("percentage", this.percentage.toJSONObject())
            }
            is BodyTemperatureRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("temperature", this.temperature.toJSONObject())
                obj.put("measurementLocation", this.measurementLocation.toBodyTemperatureMeasurementLocationString())
            }
            is DistanceRecord -> {
                obj.put("startTime", this.startTime)
                obj.put("startZoneOffset", this.startZoneOffset?.toJSONValue())
                obj.put("endTime", this.endTime)
                obj.put("endZoneOffset", this.endZoneOffset?.toJSONValue())
                obj.put("distance", this.distance.toJSONObject())
            }
            is HeartRateRecord -> {
                obj.put("startTime", this.startTime)
                obj.put("startZoneOffset", this.startZoneOffset?.toJSONValue())
                obj.put("endTime", this.endTime)
                obj.put("endZoneOffset", this.endZoneOffset?.toJSONValue())
                obj.put("samples", this.samples.toHeartRateRecordSamplesJSONArray())
            }
            is HeightRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("height", this.height.toJSONObject())
            }
            is OxygenSaturationRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("percentage", this.percentage.toJSONObject())
            }
            is RespiratoryRateRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("rate", this.rate)
            }
            is RestingHeartRateRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("beatsPerMinute", this.beatsPerMinute)
            }
            is StepsRecord -> {
                obj.put("startTime", this.startTime)
                obj.put("startZoneOffset", this.startZoneOffset?.toJSONValue())
                obj.put("endTime", this.endTime)
                obj.put("endZoneOffset", this.endZoneOffset?.toJSONValue())
                obj.put("count", this.count)
            }
            is WeightRecord -> {
                obj.put("time", this.time)
                obj.put("zoneOffset", this.zoneOffset?.toJSONValue())
                obj.put("weight", this.weight.toJSONObject())
            }
            else -> throw IllegalArgumentException("Unexpected record class: $${this::class.qualifiedName}")
        }
    }
}

internal fun Metadata.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("id", this.id)
        obj.put("clientRecordId", this.clientRecordId)
        obj.put("clientRecordVersion", this.clientRecordVersion)
        obj.put("lastModifiedTime", this.lastModifiedTime)
        obj.put("dataOrigin", this.dataOrigin.packageName)
    }
}

internal fun Change.toJSObject(): JSObject {
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

internal fun JSONObject.getInstant(name: String): Instant {
    return Instant.parse(this.getString(name))
}

internal fun JSONObject.getZoneOffsetOrNull(name: String): ZoneOffset? {
    return if (this.has(name))
        ZoneOffset.of(this.getString(name))
    else
        null
}

internal fun ZoneOffset.toJSONValue(): String {
    return this.id
}

internal fun JSONObject.getLength(name: String): Length {
    val obj = requireNotNull(this.getJSONObject(name))
    val unit = obj.getString("unit")
    val value = obj.getDouble("value")
    return when (unit) {
        "meter" -> Length.meters(value)
        "kilometer" -> Length.kilometers(value)
        "mile" -> Length.miles(value)
        "inch" -> Length.inches(value)
        "feet" -> Length.feet(value)
        else -> throw IllegalArgumentException("Unexpected mass unit: $unit")
    }
}

internal fun Length.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "meter") // TODO: support other units
        obj.put("value", this.inMeters)
    }
}

internal fun JSONObject.getMass(name: String): Mass {
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

internal fun Mass.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "gram") // TODO: support other units
        obj.put("value", this.inGrams)
    }
}

internal fun BloodGlucose.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "milligramsPerDeciliter") // TODO: support other units
        obj.put("value", this.inMilligramsPerDeciliter)
    }
}

internal fun JSONObject.getBloodGlucose(name: String): BloodGlucose {
    val obj = requireNotNull(this.getJSONObject(name))

    val value = obj.getDouble("value")
    return when (val unit = obj.getString("unit")) {
        "milligramsPerDeciliter" -> BloodGlucose.milligramsPerDeciliter(value)
        "millimolesPerLiter" -> BloodGlucose.millimolesPerLiter(value)
        else -> throw RuntimeException("Invalid BloodGlucose unit: $unit")
    }
}

internal fun Energy.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "calories") // TODO: support other units
        obj.put("value", this.inCalories)
    }
}

internal fun JSONObject.getEnergy(name: String): Energy {
    val obj = requireNotNull(this.getJSONObject(name))

    val value = obj.getDouble("value")
    return when (val unit = obj.getString("unit")) {
        "calories" -> Energy.calories(value)
        "kilocalories" -> Energy.kilocalories(value)
        "joules" -> Energy.joules(value)
        "kilojoules" -> Energy.kilojoules(value)
        else -> throw RuntimeException("Invalid Energy unit: $unit")
    }
}

internal fun Temperature.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "celsius") // TODO: support other units
        obj.put("value", this.inCelsius)
    }
}

internal fun JSONObject.getTemperature(name: String): Temperature {
    val obj = requireNotNull(this.getJSONObject(name))

    val value = obj.getDouble("value")
    return when (val unit = obj.getString("unit")) {
        "celsius" -> Temperature.celsius(value)
        "fahrenheit" -> Temperature.fahrenheit(value)
        else -> throw RuntimeException("Invalid Temperature unit: $unit")
    }
}

internal fun Int.toBodyTemperatureMeasurementLocationString(): String {
    return MEASUREMENT_LOCATION_INT_TO_STRING_MAP.getOrDefault(this, "unknown")
}

internal fun JSONObject.getBodyTemperatureMeasurementLocationInt(name: String): Int {
    val str = requireNotNull(this.getString(name))
    return MEASUREMENT_LOCATION_STRING_TO_INT_MAP.getOrDefault(str, BodyTemperatureMeasurementLocation.MEASUREMENT_LOCATION_UNKNOWN)
}

internal fun Power.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "kilocaloriesPerDay") // TODO: support other units
        obj.put("value", this.inKilocaloriesPerDay)
    }
}

internal fun JSONObject.getPower(name: String): Power {
    val obj = requireNotNull(this.getJSONObject(name))

    val value = obj.getDouble("value")
    return when (val unit = obj.getString("unit")) {
        "kilocaloriesPerDay" -> Power.kilocaloriesPerDay(value)
        "watts" -> Power.watts(value)
        else -> throw RuntimeException("Invalid Power unit: $unit")
    }
}

internal fun Pressure.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("unit", "millimetersOfMercury") // TODO: support other units
        obj.put("value", this.inMillimetersOfMercury)
    }
}

internal fun JSONObject.getPressure(name: String): Pressure {
    val obj = requireNotNull(this.getJSONObject(name))

    val value = obj.getDouble("value")
    return when (val unit = obj.getString("unit")) {
        "millimetersOfMercury" -> Pressure.millimetersOfMercury(value)
        else -> throw RuntimeException("Invalid Pressure unit: $unit")
    }
}

internal fun JSONObject.getTimeRangeFilter(name: String): TimeRangeFilter {
    val obj = requireNotNull(this.getJSONObject(name))
    return when (val type = obj.getString("type")) {
        "before" -> TimeRangeFilter.before(obj.getInstant("time"))
        "after" -> TimeRangeFilter.after(obj.getInstant("time"))
        "between" -> TimeRangeFilter.between(obj.getInstant("startTime"), obj.getInstant("endTime"))
        else -> throw IllegalArgumentException("Unexpected TimeRange type: $type")
    }
}

internal fun JSObject.getDataOriginFilter(name: String): Set<DataOrigin> {
    return this.optJSONArray(name)?.toList<String>()?.map { DataOrigin(it) }?.toSet() ?: emptySet()
}

internal fun HeartRateRecord.Sample.toJSONObject(): JSONObject {
    return JSONObject().also { jsonObject ->
        jsonObject.put("time", this.time)
        jsonObject.put("beatsPerMinute", this.beatsPerMinute)
    }
}

internal fun List<HeartRateRecord.Sample>.toHeartRateRecordSamplesJSONArray(): JSONArray {
    return JSONArray().also { jsonArray ->
        this.forEach { sample ->
            jsonArray.put(sample.toJSONObject())
        }
    }
}

internal fun JSONObject.getHeartRateRecordSamplesList(name: String): List<HeartRateRecord.Sample> {
    val jsonArray = this.getJSONArray(name)
    return jsonArray.toList<JSONObject>().map { jsonObj ->
        HeartRateRecord.Sample(
            time = jsonObj.getInstant("time"),
            beatsPerMinute = jsonObj.getLong("beatsPerMinute")
        )
    }
}

internal fun Percentage.toJSONObject(): JSONObject {
    return JSONObject().also { obj ->
        obj.put("value", this.value)
    }
}

internal fun JSONObject.getPercentage(name: String): Percentage {
    val obj = requireNotNull(this.getJSONObject(name))
    val value = obj.getDouble("value")
    return Percentage(value)
}
