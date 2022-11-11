export interface HealthConnectPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  requestPermission(): Promise<{ permission: 'granted' | 'denied' }>;
}
