import Api from '@/core/api/Api.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';

export default class ParserController {
  static async loadAll() {
    const result = await Api.api('parser/parser/list');
    return result.map((e) => new ParserModel(e));
  }

  static async load(id: number) {
    const result = await Api.api('parser/parser/get', { id });
    return new ParserModel(result);
  }

  static save(value: ParserModel) {
    return value.id
      ? Api.api('parser/parser/set', value.getDTO())
      : Api.api('parser/parser/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('parser/parser/del', { id });
  }

  static loadHTMLRaw(url: string, cookie: string = ''): Promise<string> {
    return Api.api('parser/html/download', {
      url,
      cookie,
    });
  }
}
