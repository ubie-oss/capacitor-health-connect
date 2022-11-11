export interface HealthConnectPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  insertRecords(options: {
    records: Record[];
  }): Promise<{ recordIds: string[] }>;
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

export type Mass = {
  unit: 'gram' | 'kilogram' | 'milligram' | 'microgram' | 'ounce' | 'pound';
  value: number;
};
