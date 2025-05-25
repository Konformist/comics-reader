import type { IApi } from '@/plugins/WebApiPlugin.ts';
import WebApi from '@/plugins/WebApiPlugin.ts';
import { Toast } from '@capacitor/toast';

export default {
  async api<K extends keyof IApi>(path: K, body: IApi[K]['request'] = {}): Promise<IApi[K]['response']> {
    const result = await WebApi.api({
      path,
      body,
    });

    if ('error' in result) {
      Toast.show({ text: result['error'] });
      throw new Error(result['error']);
    }

    return result.result;
  },
};
