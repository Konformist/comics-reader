import Api from '@/core/api/Api.ts';

export default class ComicCoverController {
  static saveFile(comicId: number, file: string, optimization?: boolean) {
    return Api.api('file/comic-cover/add', {
      comicId,
      file,
      optimization,
    });
  }
}
