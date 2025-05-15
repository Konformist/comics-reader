import type { IComicImageDTO } from '@/core/entities/comic/ComicTypes.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import server from '@/core/middleware/server.ts';
import { base64ToFile } from '@/core/utils/image.ts';
import { CapacitorHttp } from '@capacitor/core';
import { Filesystem } from '@capacitor/filesystem';
import { Directory } from '@capacitor/filesystem/dist/esm/definitions';

export default class ParserController {
  static async loadAll(): Promise<ParserModel[]> {
    const result = await server.getParsersAll();

    return result.map((e) => new ParserModel(e));
  }

  static async load(id: number): Promise<ParserModel> {
    const result = await server.getParser(id);

    return new ParserModel(result);
  }

  static save(model: ParserModel): Promise<number|void> {
    return model.id
      ? server.setParser(model.getDTO())
      : server.addParser(model.getDTO());
  }

  static delete(id: number): Promise<void> {
    return server.delParser(id);
  }

  static async loadComicRaw(url: string): Promise<string> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_SITE, '/test-url')
      : url;
    const result = await CapacitorHttp.get({ url: newUrl });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return result.data as string;
  }

  static async loadImageRaw(url: string): Promise<File> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_IMAGE_SITE, '/test-image-url')
      : url;

    const result = await CapacitorHttp.get({
      url: newUrl,
      responseType: 'blob',
    });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return base64ToFile(result.data);
  }

  /// old
  static async loadComic(url: string): Promise<string> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_SITE, '/test-url')
      : url;
    const result = await CapacitorHttp.get({ url: newUrl });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return result.data as string;
  }

  static async loadImage(url: string): Promise<Blob> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_IMAGE_SITE, '/test-image-url')
      : url;

    const result = await CapacitorHttp.get({
      url: newUrl,
      responseType: 'blob',
    });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return result.data as Blob;
  }

  static async writeFS(path: string, file: Blob): Promise<string> {
    const ret = await Filesystem.writeFile({
      path,
      directory: Directory.Data,
      data: file,
      recursive: true,
    });

    return ret.uri;
  }

  static getExtension(v: string) {
    if (v.includes('.jpg')) return 'jpg';
    else if (v.includes('.jpeg')) return 'jpeg';
    else if (v.includes('.gif')) return 'gif';
    else if (v.includes('.png')) return 'png';
    else if (v.includes('.webp')) return 'webp';
    else return '';
  }

  static writeFSCover(id: number, ext: string, blob: Blob) {
    return ParserController.writeFS(`${id}/cover.${ext}`, blob);
  }

  static writeFSPage(id: number, image: IComicImageDTO, blob: Blob) {
    const ext = ParserController.getExtension(image.from);
    return ParserController.writeFS(`${id}/${image.id}.${ext}`, blob);
  }

  static deleteFS(uri: string) {
    return Filesystem.deleteFile({ path: uri });
  }
}
