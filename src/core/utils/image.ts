import { Capacitor } from '@capacitor/core';
import { Filesystem } from '@capacitor/filesystem';

export const getFileUrl = async (url: string) => {
  try {
    const converted = Capacitor.convertFileSrc(url);
    const stat = await Filesystem.stat({ path: url });

    return `${converted}?${stat.size}`
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    return url;
  }
}
