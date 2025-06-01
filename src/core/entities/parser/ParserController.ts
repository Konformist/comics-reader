import Api from '@/core/api/Api.ts';
import ParserModel from '@/core/entities/parser/ParserModel.ts';
import { fileToBase64 } from '@/core/utils/image.ts';
import { getDomain } from '@/core/utils/urlUtils.ts';
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

  static async loadHTMLRaw(url: string, cookie: string = ''): Promise<string> {
    const result = await CapacitorHttp.get({
      url,
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36',
        'Accept-Language': 'ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7',
        'Cookie': cookie,
        'Priority': 'u=0, i',
        'Referer': getDomain(url) || url,
        'sec-ch-ua': '"Chromium";v="136", "Google Chrome";v="136", "Not.A/Brand";v="99"',
        'sec-ch-ua-arch': '"x86"',
        'sec-ch-ua-bitness': '"64"',
        'sec-ch-ua-full-version': '"136.0.7103.114"',
        'sec-ch-ua-full-version-list': '"Chromium";v="136.0.7103.114", "Google Chrome";v="136.0.7103.114", "Not.A/Brand";v="99.0.0.0"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-model': '""',
        'sec-ch-ua-platform': '"Windows"',
        'sec-ch-ua-platform-version': '"19.0.0"',
        'sec-fetch-dest': 'document',
        'sec-fetch-mode': 'navigate',
        'sec-fetch-site': 'same-origin',
        'sec-fetch-user': '?1',
        'upgrade-insecure-requests': '1',
      },
    });

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
