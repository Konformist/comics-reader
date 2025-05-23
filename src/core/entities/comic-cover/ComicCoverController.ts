import WebApi from '@/plugins/WebApiPlugin.ts';

export default class ComicCoverController {
  static saveFile(comicId: number, file: string, optimization?: boolean) {
    return WebApi.addCoverFile({
      comicId,
      file,
      optimization,
    });
  }
}
