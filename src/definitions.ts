export interface HealthConnectPlugin {
  checkAvailability(): Promise<{ availability: HealthConnectAvailability }>;
  insertRecords(options: { records: Omit<Record, 'metadata'>[] }): Promise<{ recordIds: string[] }>;
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
  getChanges(options: { token: string }): Promise<{ changes: Change[]; nextToken: string }>;
  requestHealthPermissions(options: { read: RecordType[]; write: RecordType[] }): Promise<{
    grantedPermissions: string[];
    hasAllPermissions: boolean;
  }>;
  checkHealthPermissions(options: { read: RecordType[]; write: RecordType[] }): Promise<{
    grantedPermissions: string[];
    hasAllPermissions: boolean;
  }>;
  revokeHealthPermissions(): Promise<void>;
}

export type HealthConnectAvailability = 'Available' | 'NotInstalled' | 'NotSupported';

export type RecordType = 'Weight' | 'Steps' | 'BloodGlucose';

type RecordBase = {
  metadata: RecordMetadata;
};
export type Record = RecordBase &
  (
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
      }
    | {
        type: 'BloodGlucose';
        time: Date;
        zoneOffset?: string;
        level: BloodGlucose;
        specimenSource:
          | 'unknown'
          | 'interstitial_fluid'
          | 'capillary_blood'
          | 'plasma'
          | 'serum'
          | 'tears'
          | 'whole_blood';
        mealType: 'unknown' | 'breakfast' | 'lunch' | 'dinner' | 'snack';
        relationToMeal: 'unknown' | 'general' | 'fasting' | 'before_meal' | 'after_meal';
      }
  );

export type RecordMetadata = {
  id: string;
  clientRecordId?: string;
  clientRecordVersion: number;
  lastModifiedTime: Date;
  dataOrigin: string;
};

export type Change =
  | {
      type: 'Upsert';
      record: Record;
    }
  | {
      type: 'Delete';
      recordId: string;
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

export type BloodGlucose = {
  unit: 'milligramsPerDeciliter' | 'millimolesPerLiter';
  value: number;
};
