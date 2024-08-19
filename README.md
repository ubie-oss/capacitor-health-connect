# capacitor-health-connect

Android Health Connect integration for Capacitor

## Install

```bash
npm install capacitor-health-connect
npx cap sync android
```

## Usage

```
import { HealthConnect } from 'capacitor-health-connect';

const healthConnectAvailability = await HealthConnect.checkAvailability();
```

## API

<docgen-index>

* [`checkAvailability()`](#checkavailability)
* [`insertRecords(...)`](#insertrecords)
* [`readRecord(...)`](#readrecord)
* [`readRecords(...)`](#readrecords)
* [`getChangesToken(...)`](#getchangestoken)
* [`getChanges(...)`](#getchanges)
* [`requestHealthPermissions(...)`](#requesthealthpermissions)
* [`checkHealthPermissions(...)`](#checkhealthpermissions)
* [`revokeHealthPermissions()`](#revokehealthpermissions)
* [`openHealthConnectSetting()`](#openhealthconnectsetting)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkAvailability()

```typescript
checkAvailability() => any
```

**Returns:** <code>any</code>

--------------------


### insertRecords(...)

```typescript
insertRecords(options: { records: Record[]; }) => any
```

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ records: {}; }</code> |

**Returns:** <code>any</code>

--------------------


### readRecord(...)

```typescript
readRecord(options: { type: RecordType; recordId: string; }) => any
```

| Param         | Type                                                                           |
| ------------- | ------------------------------------------------------------------------------ |
| **`options`** | <code>{ type: <a href="#recordtype">RecordType</a>; recordId: string; }</code> |

**Returns:** <code>any</code>

--------------------


### readRecords(...)

```typescript
readRecords(options: { type: RecordType; timeRangeFilter: TimeRangeFilter; dataOriginFilter?: string[]; ascendingOrder?: boolean; pageSize?: number; pageToken?: string; }) => any
```

| Param         | Type                                                                                                                                                                                                                  |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ type: <a href="#recordtype">RecordType</a>; timeRangeFilter: <a href="#timerangefilter">TimeRangeFilter</a>; dataOriginFilter?: {}; ascendingOrder?: boolean; pageSize?: number; pageToken?: string; }</code> |

**Returns:** <code>any</code>

--------------------


### getChangesToken(...)

```typescript
getChangesToken(options: { types: RecordType[]; }) => any
```

| Param         | Type                        |
| ------------- | --------------------------- |
| **`options`** | <code>{ types: {}; }</code> |

**Returns:** <code>any</code>

--------------------


### getChanges(...)

```typescript
getChanges(options: { token: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ token: string; }</code> |

**Returns:** <code>any</code>

--------------------


### requestHealthPermissions(...)

```typescript
requestHealthPermissions(options: { read: RecordType[]; write: RecordType[]; }) => any
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ read: {}; write: {}; }</code> |

**Returns:** <code>any</code>

--------------------


### checkHealthPermissions(...)

```typescript
checkHealthPermissions(options: { read: RecordType[]; write: RecordType[]; }) => any
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ read: {}; write: {}; }</code> |

**Returns:** <code>any</code>

--------------------


### revokeHealthPermissions()

```typescript
revokeHealthPermissions() => any
```

**Returns:** <code>any</code>

--------------------


### openHealthConnectSetting()

```typescript
openHealthConnectSetting() => any
```

**Returns:** <code>any</code>

--------------------


### Type Aliases


#### HealthConnectAvailability

<code>'Available' | 'NotInstalled' | 'NotSupported'</code>


#### Record

<code>{ type: 'ActiveCaloriesBurned'; startTime: Date; startZoneOffset?: string; endTime: Date; endZoneOffset?: string; energy: <a href="#energy">Energy</a>; } | { type: 'BasalBodyTemperature'; time: Date; zoneOffset?: string; temperature: <a href="#temperature">Temperature</a>; measurementLocation: | 'unknown' | 'armpit' | 'finger' | 'forehead' | 'mouth' | 'rectum' | 'temporal_artery' | 'toe' | 'ear' | 'wrist' | 'vagina'; } | { type: 'BasalMetabolicRate'; time: Date; zoneOffset?: string; basalMetabolicRate: <a href="#power">Power</a>; } | { type: '<a href="#bloodglucose">BloodGlucose</a>'; time: Date; zoneOffset?: string; level: <a href="#bloodglucose">BloodGlucose</a>; specimenSource: | 'unknown' | 'interstitial_fluid' | 'capillary_blood' | 'plasma' | 'serum' | 'tears' | 'whole_blood'; mealType: 'unknown' | 'breakfast' | 'lunch' | 'dinner' | 'snack'; relationToMeal: 'unknown' | 'general' | 'fasting' | 'before_meal' | 'after_meal'; } | { type: 'BloodPressure'; time: Date; zoneOffset?: string; systolic: <a href="#pressure">Pressure</a>; diastolic: <a href="#pressure">Pressure</a>; bodyPosition: 'unknown' | 'standing_up' | 'sitting_down' | 'lying_down' | 'reclining'; measurementLocation: 'unknown' | 'left_wrist' | 'right_wrist' | 'left_upper_arm' | 'right_upper_arm'; } | { type: 'BodyFat'; time: Date; zoneOffset?: string; percentage: <a href="#percentage">Percentage</a>; } | { type: 'BodyTemperature'; time: Date; zoneOffset?: string; temperature: Temperature; measurementLocation: 'unknown' | 'armpit' | 'finger' | 'forehead' | 'mouth' | 'rectum' | 'temporal_artery' | 'toe' | 'ear' | 'wrist' | 'vagina'; } | { type: 'HeartRateSeries'; startTime: Date; startZoneOffset?: string; endTime: Date; endZoneOffset?: string; samples: <a href="#heartratesample">HeartRateSample[]</a>; } | { type: 'Height'; time: Date; zoneOffset?: string; height: <a href="#length">Length</a>; } | { type: 'OxygenSaturation'; time: Date; zoneOffset?: string; percentage: <a href="#percentage">Percentage</a>; } | { type: 'RespiratoryRate'; time: Date; zoneOffset?: string; rate: number; } | { type: 'RestingHeartRate'; time: Date; zoneOffset?: string; beatsPerMinute: number; } | { type: 'Steps'; startTime: Date; startZoneOffset?: string; endTime: Date; endZoneOffset?: string; count: number; } | { type: 'Weight'; time: Date; zoneOffset?: string; weight: <a href="#mass">Mass</a>; }</code>


#### Energy

<code>{ unit: 'calories' | 'kilocalories' | 'joules' | 'kilojoules'; value: number; }</code>


#### HeartRateSample

<code>{ time: Date; beatsPerMinute: number; }</code>


#### Temperature

<code>{ unit: 'celsius' | 'fahrenheit'; value: number; }</code>


#### Percentage

<code>{ value: number; }</code>


#### Power

<code>{ unit: 'kilocaloriesPerDay' | 'watts'; value: number; }</code>


#### BloodGlucose

<code>{ unit: 'milligramsPerDeciliter' | 'millimolesPerLiter'; value: number; }</code>


#### Pressure

<code>{ unit: 'millimetersOfMercury'; value: number; }</code>


#### Length

<code>{ unit: 'meter' | 'kilometer' | 'mile' | 'inch' | 'feet'; value: number; }</code>


#### Mass

<code>{ unit: 'gram' | 'kilogram' | 'milligram' | 'microgram' | 'ounce' | 'pound'; value: number; }</code>


#### RecordType

<code>'ActiveCaloriesBurned' | 'BasalBodyTemperature' | 'BasalMetabolicRate' | '<a href="#bloodglucose">BloodGlucose</a>' | 'BloodPressure' | 'BodyFat' | 'BodyTemperature' | 'HeartRateSeries' | 'Height' | 'OxygenSaturation' | 'RespiratoryRate' | 'RestingHeartRate' | 'Steps' | 'Weight'</code>


#### StoredRecord

<code><a href="#recordbase">RecordBase</a> & <a href="#record">Record</a></code>


#### RecordBase

<code>{ metadata: <a href="#recordmetadata">RecordMetadata</a>; }</code>


#### RecordMetadata

<code>{ id: string; clientRecordId?: string; clientRecordVersion: number; lastModifiedTime: Date; dataOrigin: string; }</code>


#### TimeRangeFilter

<code>{ type: 'before' | 'after'; time: Date; } | { type: 'between'; startTime: Date; endTime: Date; }</code>


#### Change

<code>{ type: 'Upsert'; record: <a href="#record">Record</a>; } | { type: 'Delete'; recordId: string; }</code>

</docgen-api>
