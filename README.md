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


#### RecordType

<code>'Weight' | 'Steps'</code>

</docgen-api>
