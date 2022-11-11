export interface HealthConnectPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
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
