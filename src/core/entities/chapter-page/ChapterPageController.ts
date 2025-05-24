import Api from '@/core/api/Api.ts';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';

export default class ChapterPageController {
  static save(value: ChapterPageModel) {
    return value.id
      ? Api.api('chapter/page/set', value.getDTO())
      : Api.api('chapter/page/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('chapter/page/del', { id });
  }

  static saveFile(chapterPageId: number, file: string, optimization?: boolean) {
    return Api.api('file/chapter-page/add', {
      chapterPageId,
      file,
      optimization,
    });
  }
}
