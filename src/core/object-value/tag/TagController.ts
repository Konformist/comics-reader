import server from '@/core/middleware/server.ts';
import TagObject from '@/core/object-value/tag/TagObject.ts';
import { sortString } from '@/core/utils/array.ts';

export default class TagController {
  static async loadAll() {
    const result = await server.getTagsAll();

    return result
      .map((e) => new TagObject(e))
      .sort((a, b) => sortString(a.name, b.name));
  }

  static async load(id: number) {
    const result = await server.getTag(id);

    return new TagObject(result);
  }

  static async save(value: TagObject) {
    return value.id
      ? server.setTag(value.getDTO())
      : server.addTag(value.getDTO());
  }

  static async delete(id: number) {
    return server.delTag(id);
  }
}
