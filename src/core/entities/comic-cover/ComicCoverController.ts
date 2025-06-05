import Api from '@/core/api/Api.ts';

export default class ComicCoverController {
  static downloadFile(comicId: number, link: string) {
    return Api.api('file/comic-cover/download', {
      comicId,
      link,
    });
  }

  static saveFile(comicId: number) {
    return Api.api('file/comic-cover/add', { comicId });
  }
}
