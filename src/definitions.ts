export interface HealthConnectPlugin {
  checkAvailability(): Promise<{ availability: HealthConnectAvailability }>;
  insertRecords(options: {
    records: Omit<Record, 'metadata'>[];
  }): Promise<{ recordIds: string[] }>;
  readRecord(options: { type: RecordType; recordId: string }): Promise<{
    record: Record;
  }>;
  readRecords(options: {
    type: RecordType;
    timeRangeFilter: TimeRangeFilter;
    dataOriginFilter?: string[];
    ascendingOrder?: boolean;
    pageSize?: number;
    pageToken?: string;
  }): Promise<{
    records: Record[];
    pageToken?: string;
  }>;
  getChangesToken(options: { types: RecordType[] }): Promise<{ token: string }>;
  requestHealthPermissions(options: {
    read: RecordType[];
    write: RecordType[];
  }): Promise<{
    grantedPermissions: {
      read: RecordType[];
      write: RecordType[];
    };
    hasAllPermissions: boolean;
  }>;
}

export type HealthConnectAvailability =
  | 'Available'
  | 'NotInstalled'
  | 'NotSupported';

export type RecordType = 'Weight' | 'Steps';

export type Record =
  | {
      type: 'Weight';
      time: Date;
      zoneOffset?: string;
      weight: Mass;
    }
  | {
      type: 'Steps';
      startTime: Date;
      startZoneOffset?: string;
      endTime: Date;
      endZoneOffset?: string;
      count: number;
    };

export type RecordMetadata = {
  id: string;
  clientRecordId?: string;
  clientRecordVersion: number;
  lastModifiedTime: Date;
  dataOrigin: string;
};

export type TimeRangeFilter =
  | {
      type: 'before' | 'after';
      time: Date;
    }
  | {
      type: 'between';
      startTime: Date;
      endTime: Date;
    };

export type Mass = {
  unit: 'gram' | 'kilogram' | 'milligram' | 'microgram' | 'ounce' | 'pound';
  value: number;
};
