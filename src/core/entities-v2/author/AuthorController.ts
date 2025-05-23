import WebApi from '@/plugins/WebApiPlugin.ts';
import AuthorModel from '@/core/entities-v2/author/AuthorModel.ts';

export default class TagController {
  static async loadAll() {
    const { result } = await WebApi.getAuthorsAll();

    return result.map((e) => new AuthorModel(e));
  }

  static async save(value: AuthorModel) {
    return value.id
      ? WebApi.setAuthor(value.getDTO())
      : (await WebApi.addAuthor(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delAuthor({ id });
  }
}
