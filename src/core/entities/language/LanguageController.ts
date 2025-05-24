import Api from '@/core/api/Api.ts';
import LanguageModel from '@/core/entities/language/LanguageModel.ts';

export default class LanguageController {
  static async loadAll() {
    const result = await Api.api('language/language/list');
    return result.map((e) => new LanguageModel(e));
  }

  static save(value: LanguageModel) {
    return value.id
      ? Api.api('language/language/set', value.getDTO())
      : Api.api('language/language/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('language/language/del', { id });
  }
}
