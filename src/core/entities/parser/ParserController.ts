import Api from '@/core/api/Api.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { fileToBase64 } from '@/core/utils/image.ts';
import { CapacitorHttp } from '@capacitor/core';

export default class ParserController {
  static async loadAll() {
    const result = await Api.api('parser/parser/list');
    return result.map((e) => new ParserModel(e));
  }

  static async load(id: number) {
    const result = await Api.api('parser/parser/get', { id });
    return new ParserModel(result);
  }

  static save(value: ParserModel) {
    return value.id
      ? Api.api('parser/parser/set', value.getDTO())
      : Api.api('parser/parser/add', value.getDTO());
  }

  static remove(id: number) {
    return Api.api('parser/parser/del', { id });
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
