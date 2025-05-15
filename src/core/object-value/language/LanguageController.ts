import { sortString } from '@/core/utils/array.ts';
import server from '@/core/middleware/server.ts';
import LanguageObject from '@/core/object-value/language/LanguageObject.ts';

export default class LanguageController {
  static async loadAll() {
    const result = await server.getLanguagesAll();

    return result
      .map((e) => new LanguageObject(e))
      .sort((a, b) => sortString(a.name, b.name));
  }

  static async load(id: number) {
    const result = await server.getLanguage(id);

    return new LanguageObject(result);
  }

  static async save(value: LanguageObject) {
    return value.id
      ? server.setLanguage(value.getDTO())
      : server.addLanguage(value.getDTO());
  }

  static async delete(id: number) {
    return server.delLanguage(id);
  }
}
