import { WebPlugin } from '@capacitor/core';

import type { HealthConnectPluginPlugin, RecordType } from './definitions';

export class HealthConnectPluginWeb
  extends WebPlugin
  implements HealthConnectPluginPlugin
{
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
