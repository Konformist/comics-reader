import Api from '@/core/api/Api.ts';
import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';

export default class ChapterController {
  static async loadAll(comicId: number) {
    const result = await Api.api('chapter/chapter/list', { comicId });
    return result.map((e) => new ChapterModel(e));
  }

  static async load(id: number) {
    const result = await Api.api('chapter/chapter/get', { id });
    return new ChapterModel(result);
  }

  static save(value: ChapterModel) {
    return value.id
      ? Api.api('chapter/chapter/set', value.getDTO())
      : Api.api('chapter/chapter/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('chapter/chapter/del', { id });
  }
}
