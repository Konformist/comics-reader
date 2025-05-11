import { CapacitorHttp } from '@capacitor/core';
import { Filesystem } from '@capacitor/filesystem';
import { Directory } from '@capacitor/filesystem/dist/esm/definitions';

export default class ParserController {
  static async loadComic (url: string): Promise<string> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_SITE, '/test-url')
      : url;
    const result = await CapacitorHttp.get({ url: newUrl });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`)

    return result.data as string;
  }

  static async loadImage (url: string): Promise<Blob> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_IMAGE_SITE, '/test-image-url')
      : url;

    const result = await CapacitorHttp.get({
      url: newUrl,
      responseType: 'blob',
    });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`)

    return result.data as Blob;
  }

  static async writeFS (path: string, file: Blob): Promise<string> {
    const ret = await Filesystem.writeFile({
      path,
      directory: Directory.Data,
      data: file,
      recursive: true,
    });

    return ret.uri;
  }

  static getExtension (v: string) {
    if (v.includes('.jpg')) return 'jpg';
    else if (v.includes('.jpeg')) return 'jpeg';
    else if (v.includes('.gif')) return 'gif';
    else if (v.includes('.png')) return 'png';
    else if (v.includes('.webp')) return 'webp';
    else return '';
  }

  static writeFSCover (id: number, ext: string, blob: Blob) {
    return ParserController.writeFS(`${id}/cover.${ext}`, blob)
  }

  static writeFSPage (id: number, index: number, ext: string, blob: Blob) {
    return ParserController.writeFS(`${id}/${index + 1}.${ext}`, blob)
  }

  static deleteFS (uri: string) {
    return Filesystem.deleteFile({ path: uri })
  }
}
