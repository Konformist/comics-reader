export const base64ToFile = (src: string, name = '') => {
  return new File(
    [Uint8Array.from(atob(src), (m) => m.codePointAt(0) as number)],
    name,
  );
};

export const optimizeImage = async (file: File) => {
  const image = await createImageBitmap(file);
  const canvas = document.createElement('canvas');

  canvas.width = image.width;
  canvas.height = image.height;
  canvas.getContext('2d')?.drawImage(image, 0, 0);

  return canvas.toDataURL('image/webp');
};
