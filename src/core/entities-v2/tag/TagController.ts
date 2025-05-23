import WebApi from '@/plugins/WebApiPlugin.ts';
import TagModel from '@/core/entities-v2/tag/TagModel.ts';

export default class TagController {
  static async loadAll() {
    const { result } = await WebApi.getTagsAll();

    return result.map((e) => new TagModel(e));
  }

  static async save(value: TagModel) {
    return value.id
      ? WebApi.setTag(value.getDTO())
      : (await WebApi.addTag(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delTag({ id });
  }
}
