import type { ITreeDirectory, ITreeFile } from '@/core/entities/file/FileTypes.ts';
import { Directory, Filesystem } from '@capacitor/filesystem';

const getTreeChildren = async (path: string): Promise<Array<ITreeDirectory | ITreeFile>> => {
  const ret: Array<ITreeDirectory | ITreeFile> = [];

  const result = await Filesystem.readdir({
    path,
    directory: Directory.Data,
  });

  for (const file of result.files) {
    const fullPath = path ? `${path}/${file.name}` : file.name;

    if (file.type === 'directory') {
      ret.push({
        type: file.type,
        path: fullPath,
        name: file.name,
        children: await getTreeChildren(fullPath),
      });
    } else {
      ret.push({
        type: file.type,
        path: fullPath,
        name: file.name,
        size: file.size,
      });
    }
  }

  return ret;
};

export const getTree = async (path: string): Promise<ITreeDirectory[]> => [{
  type: 'directory',
  path,
  name: path,
  children: await getTreeChildren(path),
}];
