import { registerPlugin } from '@capacitor/core';

import type { HealthConnectPluginPlugin } from './definitions';

const HealthConnectPlugin = registerPlugin<HealthConnectPluginPlugin>(
  'HealthConnectPlugin',
  {
    web: () => import('./web').then(m => new m.HealthConnectPluginWeb()),
  },
);

export * from './definitions';
export { HealthConnectPlugin };
