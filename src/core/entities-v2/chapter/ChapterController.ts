import ChapterModel from '@/core/entities-v2/chapter/ChapterModel.ts';
import { makeLinkFromPath } from '@/core/utils/image.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ChapterController {
  static async loadAll(comicId: number) {
    const { result } = await WebApi.getChaptersAll({ comicId });

    for (const chapter of result) {
      for (const page of chapter.pages) {
        if (page.file) {
          page.file.path = await makeLinkFromPath(page.file.path);
        }
      }
    }

    return result.map((e) => new ChapterModel(e));
  }

  static async load(id: number) {
    const { result } = await WebApi.getChapter({ id });

    for (const page of result.pages) {
      if (page.file) {
        page.file.path = await makeLinkFromPath(page.file.path);
      }
    }

    return new ChapterModel(result);
  }

  static async save(value: ChapterModel) {
    return value.id
      ? WebApi.setChapter(value.getDTO())
      : (await WebApi.addChapter(value.getDTO())).result;
  }
}
