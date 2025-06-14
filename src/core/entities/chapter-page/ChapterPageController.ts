import Api from '@/core/api/Api.ts';
import type ChapterPageModel from '@/core/entities/chapter-page/ChapterPageModel.ts';

export default class ChapterPageController {
  static save(value: ChapterPageModel) {
    return value.id
      ? Api.api('chapter/page/set', value.getDTO())
      : Api.api('chapter/page/add', value.getDTO());
  }

  static async sequenceSave(value: ChapterPageModel[]) {
    const create = value.filter((e) => !e.id).map((e) => e.getDTO());
    const update = value.filter((e) => e.id).map((e) => e.getDTO());

    return [
      ...(await Api.sequenceApi('chapter/page/add', create)),
      ...(await Api.sequenceApi('chapter/page/set', update)),
    ];
  }

  static remove(id: number) {
    return Api.api('chapter/page/del', { id });
  }

  static sequenceRemove(ids: number[]) {
    return Api.sequenceApi('chapter/page/del', ids.map((e) => ({ id: e })));
  }

  static downloadFile(chapterPageId: number, link: string) {
    return Api.api('file/chapter-page/download', {
      chapterPageId,
      link,
    });
  }

  static saveFile(chapterPageId: number) {
    return Api.api('file/chapter-page/add', { chapterPageId });
  }
}
