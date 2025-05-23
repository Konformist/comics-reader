import { Capacitor } from '@capacitor/core';
import { Directory, Filesystem } from '@capacitor/filesystem';

export const fileToBase64 = (file: File) => (
  new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
    reader.readAsDataURL(file);
  })
);

export const makeLinkFromPath = async (path: string) => {
  const { uri } = await Filesystem.getUri({ path, directory: Directory.Data });
  return Capacitor.convertFileSrc(uri);
};
