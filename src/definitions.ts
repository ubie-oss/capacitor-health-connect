export interface HealthConnectPlugin {
  checkAvailability(): Promise<{ availability: HealthConnectAvailability }>;
  insertRecords(options: {
    records: Record[];
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
