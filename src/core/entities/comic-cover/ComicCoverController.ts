import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ComicCoverController {
  static saveFile(comicId: number, file: string) {
    return WebApi.addCoverFile({
      comicId,
      file,
    });
  }
}
