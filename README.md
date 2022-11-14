# capacitor-health-connect

Android Health Connect integration for Capacitor

## Install

```bash
npm install capacitor-health-connect
npx cap sync
```

## API

<docgen-index>

* [`checkAvailability()`](#checkavailability)
* [`insertRecords(...)`](#insertrecords)
* [`readRecord(...)`](#readrecord)
* [`readRecords(...)`](#readrecords)
* [`requestHealthPermissions(...)`](#requesthealthpermissions)
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


### requestHealthPermissions(...)

```typescript
requestHealthPermissions(options: { read: RecordType[]; write: RecordType[]; }) => any
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

<code>{ type: 'Weight'; time: Date; zoneOffset?: string; weight: <a href="#mass">Mass</a>; } | { type: 'Steps'; startTime: Date; startZoneOffset?: string; endTime: Date; endZoneOffset?: string; count: number; }</code>


#### Mass

<code>{ unit: 'gram' | 'kilogram' | 'milligram' | 'microgram' | 'ounce' | 'pound'; value: number; }</code>


#### RecordType

<code>'Weight' | 'Steps'</code>


#### TimeRangeFilter

<code>{ type: 'before' | 'after'; time: Date; } | { type: 'between'; startTime: Date; endTime: Date; }</code>

</docgen-api>
