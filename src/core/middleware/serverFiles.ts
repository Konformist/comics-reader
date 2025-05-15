import type { IDirectory, IFile } from '@/core/entities/file/FileTypes.ts';
import { fileToBase64, getFileUrl } from '@/core/utils/image.ts';
import { ImageManipulator } from '@capacitor-community/image-manipulator';
import { Directory, Filesystem } from '@capacitor/filesystem';

const addFile = async (path: string, file: File): Promise<string> => {
  const ret = await Filesystem.writeFile({
    path,
    directory: Directory.Data,
    data: await fileToBase64(file),
    recursive: true,
  });

  return getFileUrl(ret.uri);
};

const delFile = (path: string): Promise<void> => {
  return Filesystem.deleteFile({
    path,
    directory: Directory.Data,
  });
};

const resizeImage = async (
  path: string,
  options: {
    maxWidth?: number,
    maxHeight?: number,
  },
): Promise<string> => {
  const result = await ImageManipulator.resize({
    quality: 100,
    imagePath: path,
    maxWidth: options.maxWidth,
    maxHeight: options.maxHeight,
  });

  await Filesystem.rename({
    from: result.imagePath,
    to: path,
  });

  return getFileUrl(path);
};

const getTreeRecursive = async (path: string): Promise<Array<IDirectory | IFile>> => {
  const ret: Array<IDirectory | IFile> = [];

  try {
    const result = await Filesystem.readdir({ path });

    for (const file of result.files) {
      if (file.type === 'directory') {
        ret.push({
          type: file.type,
          path: file.name,
          children: await getTreeRecursive(file.uri),
        });
      } else {
        ret.push({
          type: file.type,
          path: file.name,
          size: file.size,
        });
      }
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) { /* empty */ }

  return ret;
};

/**
 * @param path - путь папки
 */
const getTree = async (path: string): Promise<IDirectory[]> => {
  try {
    const rootUri = await Filesystem.getUri({ path, directory: Directory.Data });

    return [{
      type: 'directory',
      path,
      children: await getTreeRecursive(rootUri.uri),
    }];
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    return [];
  }
};

export default {
  addFile,
  delFile,
  resizeImage,
  getTree,
};
