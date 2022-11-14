import { registerPlugin } from '@capacitor/core';

import type { HealthConnectPlugin } from './definitions';

const HealthConnect = registerPlugin<HealthConnectPlugin>('HealthConnect', {});

export * from './definitions';
export { HealthConnect };
