import type { ISettingsDTO } from '@/core/entities/settings/SettingsTypes.ts';
import { SETTINGS_STORE } from '@/core/middleware/variables.ts';
import { Preferences } from '@capacitor/preferences';

const dataRaw: { item: ISettingsDTO } = {
  item: {
    autoReading: false,
    autoReadingTimeout: 10,
    isCompress: false,
    direction: 'horizontal',
  },
};

const setSettingsData = async (): Promise<void> => {
  await Preferences.set({
    key: SETTINGS_STORE,
    value: JSON.stringify(dataRaw.item),
  });
};

const getSettingsData = async (): Promise<void> => {
  const result = await Preferences.get({ key: SETTINGS_STORE });

  if (!result.value) await setSettingsData();
  else dataRaw.item = JSON.parse(result.value);
};

const setSettings = async (value: ISettingsDTO): Promise<void> => {
  Object.assign(dataRaw.item, value);
  await setSettingsData();
};

const getSettings = async (): Promise<ISettingsDTO> => {
  await getSettingsData();

  return dataRaw.item;
};

export default {
  dataRaw,
  setSettingsData,
  getSettingsData,
  setSettings,
  getSettings,
};
