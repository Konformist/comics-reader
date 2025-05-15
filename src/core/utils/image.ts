import { Capacitor } from '@capacitor/core';
import { Filesystem } from '@capacitor/filesystem';

export const getFileUrl = async (url: string) => {
  try {
    const converted = Capacitor.convertFileSrc(url);
    const stat = await Filesystem.stat({ path: url });

    return `${converted}?${stat.size}`;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    return url;
  }
};

export const base64ToFile = (src: string, name = '') => {
  return new File(
    [Uint8Array.from(atob(src), (m) => m.codePointAt(0) as number)],
    name,
  );
};

export const optimizeImageV2 = async (file: File) => {
  const image = await createImageBitmap(file);
  const canvas = document.createElement('canvas');

  canvas.width = image.width;
  canvas.height = image.height;
  canvas.getContext('2d')?.drawImage(image, 0, 0);

  return canvas.toDataURL('image/webp', 1);
};
