import { Preferences } from '@capacitor/preferences';
import { SETTINGS_STORE } from '@/core/utils/variables.ts';
import SettingsModel from '@/core/entities/settings/SettingsModel.ts';

export default class SettingsController {
  static async load(): Promise<SettingsModel> {
    const result = await Preferences.get({ key: SETTINGS_STORE });

    if (!result.value) {
      const settings = new SettingsModel();
      await SettingsController.save(settings);
      return settings;
    } else {
      return new SettingsModel(JSON.parse(result.value));
    }
  }

  static save(settings: SettingsModel): Promise<void> {
    return Preferences.set({
      key: SETTINGS_STORE,
      value: JSON.stringify(settings.getDTO()),
    });
  }
}
