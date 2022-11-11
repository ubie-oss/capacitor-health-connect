# capacitor-health-connect

Android Health Connect integration for Capacitor

## Install

```bash
npm install capacitor-health-connect
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`insertRecords(...)`](#insertrecords)
* [`requestHealthPermissions(...)`](#requesthealthpermissions)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

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


#### Record

<code>{ type: 'Weight'; time: Date; zoneOffset?: string; weight: <a href="#mass">Mass</a>; } | { type: 'Steps'; startTime: Date; startZoneOffset?: string; endTime: Date; endZoneOffset?: string; count: number; }</code>


#### Mass

<code>{ unit: 'gram' | 'kilogram' | 'milligram' | 'microgram' | 'ounce' | 'pound'; value: number; }</code>


#### RecordType

<code>'Weight' | 'Steps'</code>

</docgen-api>
