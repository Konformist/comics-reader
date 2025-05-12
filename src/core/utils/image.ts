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

export const base64ToFile = (src: string, name = '') => {
  return new File(
    [Uint8Array.from(atob(src), m => m.codePointAt(0) as number)],
    name,
  );
}

// @todo доработать resize
export const optimizeImage = async (src: File, xMax?: number, yMax?: number) => (
  new Promise<File>((resolve, reject) => {
    const image = new Image();

    image.src = URL.createObjectURL(src);
    image.onload = () => {
      const canvas = document.createElement('canvas');

      let coef = 1;

      if ((xMax && yMax)
        && (image.naturalWidth > xMax || image.naturalHeight > yMax)) {
        coef = image.naturalHeight / image.naturalWidth < yMax / xMax
          ? xMax / image.naturalWidth
          : yMax / image.naturalHeight;
      }

      canvas.width = image.naturalWidth * coef;
      canvas.height = image.naturalHeight * coef;
      canvas.getContext('2d')?.drawImage(image, 0, 0, canvas.width, canvas.height);

      canvas.toBlob(blob => {
        if (!blob) {
          reject('Error');
        } else {
          const newImage = new File(
            [blob],
            src.name,
            { type: blob.type },
          );

          resolve(newImage);
        }
      }, 'image/webp');
    }
  })
)
