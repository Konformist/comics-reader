import Api from '@/core/api/Api.ts';
import AuthorModel from '@/core/entities/author/AuthorModel.ts';

export default class AuthorController {
  static async loadAll() {
    const result = await Api.api('author/author/list');
    return result.map((e) => new AuthorModel(e));
  }

  static save(value: AuthorModel) {
    return value.id
      ? Api.api('author/author/set', value.getDTO())
      : Api.api('author/author/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('author/author/del', { id });
  }
}
