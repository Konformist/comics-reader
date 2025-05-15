import { sortString } from '@/core/utils/array.ts';
import server from '@/core/middleware/server.ts';
import AuthorObject from '@/core/object-value/author/AuthorObject.ts';

export default class AuthorController {
  static async loadAll() {
    const result = await server.getAuthorsAll();

    return result
      .map((e) => new AuthorObject(e))
      .sort((a, b) => sortString(a.name, b.name));
  }

  static async load(id: number) {
    const result = await server.getAuthor(id);

    return new AuthorObject(result);
  }

  static async save(value: AuthorObject) {
    return value.id
      ? server.setAuthor(value.getDTO())
      : server.addAuthor(value.getDTO());
  }

  static async delete(id: number) {
    return server.delAuthor(id);
  }
}
