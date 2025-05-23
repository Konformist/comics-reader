import ParserModel from '@/core/entities-v2/parser/ParserModel.ts';
import { fileToBase64 } from '@/core/utils/image.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';
import { CapacitorHttp } from '@capacitor/core';

export default class ParserController {
  static async loadAll() {
    const { result } = await WebApi.getParsersAll();

    return result.map((e) => new ParserModel(e));
  }

  static async load(id: number) {
    const { result } = await WebApi.getParser({ id });

    return new ParserModel(result);
  }

  static async save(value: ParserModel) {
    return value.id
      ? WebApi.setParser(value.getDTO())
      : (await WebApi.addParser(value.getDTO())).result;
  }

  static remove(id: number) {
    return WebApi.delParser({ id });
  }

  static async loadComicRaw(url: string): Promise<string> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_SITE, '/test-url')
      : url;
    const result = await CapacitorHttp.get({ url: newUrl });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return result.data as string;
  }

  static async loadImageRaw(url: string): Promise<string> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_IMAGE_SITE, '/test-image-url')
      : url;

    const result = await CapacitorHttp.get({
      url: newUrl,
      responseType: 'blob',
    });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return typeof result.data === 'string' ? result.data : fileToBase64(result.data);
  }
}
