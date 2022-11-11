import { WebPlugin } from '@capacitor/core';

import type {
  HealthConnectPluginPlugin,
  Record,
  RecordType,
} from './definitions';

export class HealthConnectPluginWeb
  extends WebPlugin
  implements HealthConnectPluginPlugin
{
  readRecords(): Promise<{
    records: Record[];
    pageToken?: string | undefined;
  }> {
    throw new Error('Method not implemented.');
  }
  insertRecords(): Promise<{ recordIds: string[] }> {
    throw new Error('Method not implemented.');
  }
  requestHealthPermissions(): Promise<{
    grantedPermissions: { read: RecordType[]; write: RecordType[] };
    hasAllPermissions: boolean;
  }> {
    throw new Error('Method not implemented.');
  }
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
