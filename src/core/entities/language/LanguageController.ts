import WebApi from '@/plugins/WebApiPlugin.ts';
import LanguageModel from '@/core/entities/language/LanguageModel.ts';

export default class LanguageController {
  static async loadAll() {
    const { result } = await WebApi.getLanguagesAll();

    return result.map((e) => new LanguageModel(e));
  }

  static async save(value: LanguageModel) {
    return value.id
      ? WebApi.setLanguage(value.getDTO())
      : (await WebApi.addLanguage(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delLanguage({ id });
  }
}
