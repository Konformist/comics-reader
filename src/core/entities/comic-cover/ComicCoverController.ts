import Api from '@/core/api/Api.ts';

export default class ComicCoverController {
  static saveFile(comicId: number, file: string) {
    return Api.api('file/comic-cover/add', {
      comicId,
      file,
    });
  }
}
