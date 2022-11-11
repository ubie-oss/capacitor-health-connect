import { WebPlugin } from '@capacitor/core';

import type { HealthConnectPluginPlugin } from './definitions';

export class HealthConnectPluginWeb
  extends WebPlugin
  implements HealthConnectPluginPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
