import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ChapterPageController {
  static async save(value: ChapterPageModel) {
    return value.id
      ? WebApi.setChapterPage(value.getDTO())
      : (await WebApi.addChapterPage(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delChapterPage({ id });
  }

  static saveFile(chapterPageId: number, file: string) {
    return WebApi.addChapterPageFile({
      chapterPageId,
      file,
    });
  }
}
