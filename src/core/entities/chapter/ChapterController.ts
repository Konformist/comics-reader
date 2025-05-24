import ChapterModel from '@/core/entities/chapter/ChapterModel.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ChapterController {
  static async loadAll(comicId: number) {
    const { result } = await WebApi.getChaptersAll({ comicId });

    return result.map((e) => new ChapterModel(e));
  }

  static async load(id: number) {
    const { result } = await WebApi.getChapter({ id });

    return new ChapterModel(result);
  }

  static async save(value: ChapterModel) {
    return value.id
      ? WebApi.setChapter(value.getDTO())
      : (await WebApi.addChapter(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delChapter({ id });
  }
}
