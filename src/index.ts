import { registerPlugin } from '@capacitor/core';

import type { HealthConnectPluginPlugin } from './definitions';

const HealthConnectPlugin = registerPlugin<HealthConnectPluginPlugin>(
  'HealthConnectPlugin',
  {},
);

export * from './definitions';
export { HealthConnectPlugin };
