export interface HealthConnectPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
