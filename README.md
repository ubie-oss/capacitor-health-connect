# capacitor-health-connect

Android Health Connect integration for Capacitor

## Install

```bash
npm install @ubie-inc/capacitor-health-connect
npx cap sync
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
insertRecords(options: { records: Omit<Record, 'metadata'>[]; }) => any
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


### Type Aliases


#### HealthConnectAvailability

<code>'Available' | 'NotInstalled' | 'NotSupported'</code>


#### Record

<code><a href="#recordbase">RecordBase</a> & ( | { type: 'Weight'; time: Date; zoneOffset?: string; weight: <a href="#mass">Mass</a>; } | { type: 'Steps'; startTime: Date; startZoneOffset?: string; endTime: Date; endZoneOffset?: string; count: number; } | { type: '<a href="#bloodglucose">BloodGlucose</a>'; time: Date; zoneOffset?: string; level: <a href="#bloodglucose">BloodGlucose</a>; specimenSource: | 'unknown' | 'interstitial_fluid' | 'capillary_blood' | 'plasma' | 'serum' | 'tears' | 'whole_blood'; mealType: 'unknown' | 'breakfast' | 'lunch' | 'dinner' | 'snack'; relationToMeal: 'unknown' | 'general' | 'fasting' | 'before_meal' | 'after_meal'; } )</code>


#### RecordBase

<code>{ metadata: <a href="#recordmetadata">RecordMetadata</a>; }</code>


#### RecordMetadata

<code>{ id: string; clientRecordId?: string; clientRecordVersion: number; lastModifiedTime: Date; dataOrigin: string; }</code>


#### Mass

<code>{ unit: 'gram' | 'kilogram' | 'milligram' | 'microgram' | 'ounce' | 'pound'; value: number; }</code>


#### BloodGlucose

<code>{ unit: 'milligramsPerDeciliter' | 'millimolesPerLiter'; value: number; }</code>


#### RecordType

<code>'Weight' | 'Steps' | '<a href="#bloodglucose">BloodGlucose</a>'</code>


#### TimeRangeFilter

<code>{ type: 'before' | 'after'; time: Date; } | { type: 'between'; startTime: Date; endTime: Date; }</code>


#### Change

<code>{ type: 'Upsert'; record: <a href="#record">Record</a>; } | { type: 'Delete'; recordId: string; }</code>

</docgen-api>
