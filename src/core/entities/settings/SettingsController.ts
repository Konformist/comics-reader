import Api from '@/core/api/Api.ts';
import SettingsModel from '@/core/entities/settings/SettingsModel.ts';

export default class SettingsController {
  static async load(): Promise<SettingsModel> {
    const result = await Api.api('settings/settings/get');
    return new SettingsModel(result);
  }

  static save(settings: SettingsModel): Promise<boolean> {
    return Api.api('settings/settings/set', settings.getDTO());
  }
}
