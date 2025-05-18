import ParserModel from '@/core/entities/parser/ParserModel.ts';
import server from '@/core/middleware/server.ts';
import { fileToBase64 } from '@/core/utils/image.ts';
import { CapacitorHttp } from '@capacitor/core';

export default class ParserController {
  static async loadAll(): Promise<ParserModel[]> {
    const result = await server.getParsersAll();

    return result.map((e) => new ParserModel(e));
  }

  static async load(id: number): Promise<ParserModel> {
    const result = await server.getParser(id);

    return new ParserModel(result);
  }

  static save(model: ParserModel): Promise<number | void> {
    return model.id
      ? server.setParser(model.getDTO())
      : server.addParser(model.getDTO());
  }

  static delete(id: number): Promise<void> {
    return server.delParser(id);
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

  static async loadComicRaw(url: string): Promise<string> {
    const newUrl = import.meta.env.DEV
      ? url.replace(import.meta.env.VITE_TEST_SITE, '/test-url')
      : url;
    const result = await CapacitorHttp.get({ url: newUrl });

    if (result.status !== 200) throw new Error(`Error code: ${result.status}`);

    return result.data as string;
  }
}
