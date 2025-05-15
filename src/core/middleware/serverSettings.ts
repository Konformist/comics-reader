import type { ISettingsDTO } from '@/core/entities/settings/SettingsTypes.ts';
import { SETTINGS_STORE, SETTINGS_VERSION } from '@/core/middleware/variables.ts';
import { Preferences } from '@capacitor/preferences';

let settingsRaw: ISettingsDTO | null = null;

const setSettingsData = async (): Promise<void> => {
  await Preferences.set({
    key: SETTINGS_STORE,
    value: JSON.stringify({
      version: SETTINGS_VERSION,
      item: settingsRaw,
    }),
  });
};

const getSettingsData = async (): Promise<ISettingsDTO | undefined> => {
  const store = await Preferences.get({ key: SETTINGS_STORE });

  if (!store.value) return undefined;

  return JSON.parse(store.value).item;
};

const setSettings = async (value: ISettingsDTO): Promise<void> => {
  if (settingsRaw) Object.assign(settingsRaw, value);
  else settingsRaw = value;

  await setSettingsData();
};

const getSettings = async (): Promise<ISettingsDTO | undefined> => {
  if (!settingsRaw) return await getSettingsData();

  return settingsRaw ?? undefined;
};

export default {
  settingsRaw,
  setSettingsData,
  getSettingsData,
  setSettings,
  getSettings,
};
