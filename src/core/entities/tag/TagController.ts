import Api from '@/core/api/Api.ts';
import TagModel from '@/core/entities/tag/TagModel.ts';

export default class TagController {
  static async loadAll() {
    const result = await Api.api('tag/tag/list');
    return result.map((e) => new TagModel(e));
  }

  static save(value: TagModel) {
    return value.id
      ? Api.api('tag/tag/set', value.getDTO())
      : Api.api('tag/tag/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('tag/tag/del', { id });
  }
}
